package com.banquito.switchpagos.routing.service.impl;

import com.banquito.switchpagos.routing.dto.event.PaymentLineRejectedEvent;
import com.banquito.switchpagos.routing.dto.event.PaymentLineRequestedEvent;
import com.banquito.switchpagos.routing.dto.event.PaymentLineRoutedOffUsEvent;
import com.banquito.switchpagos.routing.dto.event.PaymentLineRoutedOnUsEvent;
import com.banquito.switchpagos.routing.enums.RoutingInstitution;
import com.banquito.switchpagos.routing.enums.RoutingStatus;
import com.banquito.switchpagos.routing.enums.RoutingType;
import com.banquito.switchpagos.routing.exception.InvalidRoutingEventException;
import com.banquito.switchpagos.routing.mapper.RoutingEventMapper;
import com.banquito.switchpagos.routing.model.PaymentLineRouting;
import com.banquito.switchpagos.routing.model.RoutingDecision;
import com.banquito.switchpagos.routing.model.RoutingError;
import com.banquito.switchpagos.routing.repository.PaymentLineRoutingRepository;
import com.banquito.switchpagos.routing.repository.RoutingDecisionRepository;
import com.banquito.switchpagos.routing.repository.RoutingErrorRepository;
import com.banquito.switchpagos.routing.service.RoutingEventPublisher;
import com.banquito.switchpagos.routing.service.RoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoutingServiceImpl implements RoutingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingServiceImpl.class);

    private final PaymentLineRoutingRepository paymentLineRoutingRepository;
    private final RoutingDecisionRepository routingDecisionRepository;
    private final RoutingErrorRepository routingErrorRepository;
    private final RoutingEventMapper routingEventMapper;
    private final RoutingEventPublisher routingEventPublisher;
    private final String routedOnUsRoutingKey;
    private final String routedOffUsRoutingKey;
    private final String lineRejectedRoutingKey;

    public RoutingServiceImpl(
            PaymentLineRoutingRepository paymentLineRoutingRepository,
            RoutingDecisionRepository routingDecisionRepository,
            RoutingErrorRepository routingErrorRepository,
            RoutingEventMapper routingEventMapper,
            RoutingEventPublisher routingEventPublisher,
            @Value("${rabbit.routing-key.routed-on-us}") String routedOnUsRoutingKey,
            @Value("${rabbit.routing-key.routed-off-us}") String routedOffUsRoutingKey,
            @Value("${rabbit.routing-key.line-rejected}") String lineRejectedRoutingKey) {
        this.paymentLineRoutingRepository = paymentLineRoutingRepository;
        this.routingDecisionRepository = routingDecisionRepository;
        this.routingErrorRepository = routingErrorRepository;
        this.routingEventMapper = routingEventMapper;
        this.routingEventPublisher = routingEventPublisher;
        this.routedOnUsRoutingKey = routedOnUsRoutingKey;
        this.routedOffUsRoutingKey = routedOffUsRoutingKey;
        this.lineRejectedRoutingKey = lineRejectedRoutingKey;
    }

    @Override
    @Transactional
    public void routePaymentLine(PaymentLineRequestedEvent event) {
        validateIdentifiers(event);

        Optional<PaymentLineRouting> existingTrace = paymentLineRoutingRepository.findByLineId(event.getLineId());
        if (existingTrace.isPresent()) {
            LOGGER.info("Linea ya procesada por routing. batchId={}, lineId={}, status={}",
                    existingTrace.get().getBatchId(), existingTrace.get().getLineId(), existingTrace.get().getStatus());
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        PaymentLineRouting routingTrace = routingEventMapper.toRoutingTrace(event, now);
        paymentLineRoutingRepository.save(routingTrace);

        try {
            validateBusinessFields(event);
            RoutingInstitution institution = RoutingInstitution.findByRoutingCode(event.getRoutingCode())
                    .orElseThrow(() -> new InvalidRoutingEventException(
                            "ROUTING_CODE_INVALIDO",
                            "Routing code no existe en el catalogo del routing-service"));
            publishRoutedEvent(routingTrace, institution, now);
        } catch (InvalidRoutingEventException exception) {
            publishRejectedEvent(routingTrace, exception.getRejectionCode(), exception.getMessage(), now);
        }
    }

    private void publishRoutedEvent(PaymentLineRouting routingTrace, RoutingInstitution institution, OffsetDateTime now) {
        UUID publishedEventId = UUID.randomUUID();
        String publishedRoutingKey = resolveRoutingKey(institution);

        routingTrace.setRoutingType(institution.getRoutingType().name());
        routingTrace.setStatus(RoutingStatus.ENRUTADA.name());
        routingTrace.setUpdatedAt(now);
        paymentLineRoutingRepository.save(routingTrace);

        if (RoutingType.ON_US.equals(institution.getRoutingType())) {
            PaymentLineRoutedOnUsEvent routedEvent = routingEventMapper.toOnUsEvent(routingTrace, institution, publishedEventId, now);
            routingEventPublisher.publishOnUs(routedEvent);
        } else {
            PaymentLineRoutedOffUsEvent routedEvent = routingEventMapper.toOffUsEvent(routingTrace, institution, publishedEventId, now);
            routingEventPublisher.publishOffUs(routedEvent);
        }

        RoutingDecision decision = routingEventMapper.toDecision(routingTrace, institution, publishedEventId, publishedRoutingKey, now);
        routingDecisionRepository.save(decision);
        LOGGER.info("Linea enrutada. batchId={}, lineId={}, routingType={}, routingKey={}",
                routingTrace.getBatchId(), routingTrace.getLineId(), routingTrace.getRoutingType(), publishedRoutingKey);
    }

    private void publishRejectedEvent(
            PaymentLineRouting routingTrace,
            String rejectionCode,
            String rejectionReason,
            OffsetDateTime now) {
        UUID publishedEventId = UUID.randomUUID();
        routingTrace.setRoutingType(RoutingType.REJECTED.name());
        routingTrace.setStatus(RoutingStatus.RECHAZADA.name());
        routingTrace.setUpdatedAt(now);
        paymentLineRoutingRepository.save(routingTrace);

        PaymentLineRejectedEvent rejectedEvent = routingEventMapper.toRejectedEvent(
                routingTrace,
                publishedEventId,
                now,
                rejectionCode,
                rejectionReason);
        routingEventPublisher.publishRejected(rejectedEvent);

        RoutingError error = routingEventMapper.toRoutingError(
                routingTrace,
                publishedEventId,
                lineRejectedRoutingKey,
                rejectionCode,
                rejectionReason,
                now);
        routingErrorRepository.save(error);
        LOGGER.info("Linea rechazada por routing. batchId={}, lineId={}, rejectionCode={}",
                routingTrace.getBatchId(), routingTrace.getLineId(), rejectionCode);
    }

    private String resolveRoutingKey(RoutingInstitution institution) {
        if (RoutingType.ON_US.equals(institution.getRoutingType())) {
            return routedOnUsRoutingKey;
        }
        return routedOffUsRoutingKey;
    }

    private void validateIdentifiers(PaymentLineRequestedEvent event) {
        if (event == null) {
            throw new InvalidRoutingEventException("EVENTO_NULO", "El evento de linea es obligatorio");
        }
        if (event.getBatchId() == null || event.getLineId() == null || event.getCorrelationId() == null) {
            throw new InvalidRoutingEventException("IDENTIFICADORES_INCOMPLETOS", "El evento debe incluir batchId, lineId y correlationId");
        }
    }

    private void validateBusinessFields(PaymentLineRequestedEvent event) {
        if (isBlank(event.getRoutingCode())) {
            throw new InvalidRoutingEventException("ROUTING_CODE_REQUERIDO", "Routing code es obligatorio");
        }
        if (isBlank(event.getCoreFundingId())) {
            throw new InvalidRoutingEventException("FONDEO_REQUERIDO", "Referencia de fondeo del Core es obligatoria");
        }
        if (isBlank(event.getDestinationAccountNumber())) {
            throw new InvalidRoutingEventException("CUENTA_DESTINO_REQUERIDA", "Cuenta destino es obligatoria");
        }
        if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRoutingEventException("MONTO_INVALIDO", "Monto de la linea debe ser mayor a cero");
        }
        if (isBlank(event.getCurrency())) {
            throw new InvalidRoutingEventException("MONEDA_REQUERIDA", "Moneda de la linea es obligatoria");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
