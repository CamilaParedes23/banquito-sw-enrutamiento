package com.banquito.switchpagos.routing.mapper;

import com.banquito.switchpagos.routing.dto.event.PaymentLineRejectedEvent;
import com.banquito.switchpagos.routing.dto.event.PaymentLineRequestedEvent;
import com.banquito.switchpagos.routing.dto.event.PaymentLineRoutedOffUsEvent;
import com.banquito.switchpagos.routing.dto.event.PaymentLineRoutedOnUsEvent;
import com.banquito.switchpagos.routing.enums.RoutingInstitution;
import com.banquito.switchpagos.routing.enums.RoutingStatus;
import com.banquito.switchpagos.routing.model.PaymentLineRouting;
import com.banquito.switchpagos.routing.model.RoutingDecision;
import com.banquito.switchpagos.routing.model.RoutingError;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class RoutingEventMapper {

    private static final String SOURCE_SERVICE = "banquito-switch-routing-service";

    public PaymentLineRouting toRoutingTrace(PaymentLineRequestedEvent event, OffsetDateTime now) {
        PaymentLineRouting routingTrace = new PaymentLineRouting(UUID.randomUUID());
        routingTrace.setSourceEventId(event.getEventId());
        routingTrace.setBatchId(event.getBatchId());
        routingTrace.setLineId(event.getLineId());
        routingTrace.setCorrelationId(event.getCorrelationId());
        routingTrace.setSequenceNumber(event.getSequenceNumber());
        routingTrace.setCompanyRuc(event.getCompanyRuc());
        routingTrace.setSourceAccountNumber(event.getSourceAccountNumber());
        routingTrace.setCoreFundingId(event.getCoreFundingId());
        routingTrace.setBeneficiaryIdentification(event.getBeneficiaryIdentification());
        routingTrace.setBeneficiaryName(event.getBeneficiaryName());
        routingTrace.setDestinationAccountNumber(event.getDestinationAccountNumber());
        routingTrace.setRoutingCode(event.getRoutingCode());
        routingTrace.setAmount(event.getAmount());
        routingTrace.setCurrency(event.getCurrency());
        routingTrace.setReference(event.getReference());
        routingTrace.setNotificationEmail(event.getNotificationEmail());
        routingTrace.setStatus(RoutingStatus.RECIBIDA.name());
        routingTrace.setCreatedAt(now);
        routingTrace.setUpdatedAt(now);
        return routingTrace;
    }

    public PaymentLineRoutedOnUsEvent toOnUsEvent(PaymentLineRouting routingTrace, RoutingInstitution institution, UUID eventId, OffsetDateTime now) {
        PaymentLineRoutedOnUsEvent event = new PaymentLineRoutedOnUsEvent();
        fillCommonRoutedEvent(event, routingTrace, institution, eventId, now, "PAYMENT_LINE_ROUTED_ON_US");
        return event;
    }

    public PaymentLineRoutedOffUsEvent toOffUsEvent(PaymentLineRouting routingTrace, RoutingInstitution institution, UUID eventId, OffsetDateTime now) {
        PaymentLineRoutedOffUsEvent event = new PaymentLineRoutedOffUsEvent();
        event.setEventId(eventId);
        event.setEventType("PAYMENT_LINE_ROUTED_OFF_US");
        event.setOccurredAt(now);
        event.setBatchId(routingTrace.getBatchId());
        event.setLineId(routingTrace.getLineId());
        event.setCorrelationId(routingTrace.getCorrelationId());
        event.setSourceService(SOURCE_SERVICE);
        event.setSequenceNumber(routingTrace.getSequenceNumber());
        event.setCompanyRuc(routingTrace.getCompanyRuc());
        event.setSourceAccountNumber(routingTrace.getSourceAccountNumber());
        event.setCoreFundingId(routingTrace.getCoreFundingId());
        event.setBeneficiaryIdentification(routingTrace.getBeneficiaryIdentification());
        event.setBeneficiaryName(routingTrace.getBeneficiaryName());
        event.setDestinationAccountNumber(routingTrace.getDestinationAccountNumber());
        event.setRoutingCode(routingTrace.getRoutingCode());
        event.setDestinationInstitutionName(institution.getInstitutionName());
        event.setAmount(routingTrace.getAmount());
        event.setCurrency(routingTrace.getCurrency());
        event.setReference(routingTrace.getReference());
        event.setNotificationEmail(routingTrace.getNotificationEmail());
        return event;
    }

    public PaymentLineRejectedEvent toRejectedEvent(
            PaymentLineRouting routingTrace,
            UUID eventId,
            OffsetDateTime now,
            String rejectionCode,
            String rejectionReason) {
        PaymentLineRejectedEvent event = new PaymentLineRejectedEvent();
        event.setEventId(eventId);
        event.setEventType("PAYMENT_LINE_REJECTED");
        event.setOccurredAt(now);
        event.setBatchId(routingTrace.getBatchId());
        event.setLineId(routingTrace.getLineId());
        event.setCorrelationId(routingTrace.getCorrelationId());
        event.setSourceService(SOURCE_SERVICE);
        event.setSequenceNumber(routingTrace.getSequenceNumber());
        event.setBeneficiaryIdentification(routingTrace.getBeneficiaryIdentification());
        event.setBeneficiaryName(routingTrace.getBeneficiaryName());
        event.setDestinationAccountNumber(routingTrace.getDestinationAccountNumber());
        event.setRoutingCode(routingTrace.getRoutingCode());
        event.setAmount(routingTrace.getAmount());
        event.setCurrency(routingTrace.getCurrency());
        event.setReference(routingTrace.getReference());
        event.setFinalStatus("RECHAZADA");
        event.setBillable(Boolean.FALSE);
        event.setRejectionCode(rejectionCode);
        event.setRejectionReason(rejectionReason);
        return event;
    }

    public RoutingDecision toDecision(
            PaymentLineRouting routingTrace,
            RoutingInstitution institution,
            UUID publishedEventId,
            String publishedRoutingKey,
            OffsetDateTime now) {
        RoutingDecision decision = new RoutingDecision(UUID.randomUUID());
        decision.setLineId(routingTrace.getLineId());
        decision.setBatchId(routingTrace.getBatchId());
        decision.setRoutingType(institution.getRoutingType().name());
        decision.setRoutingCode(routingTrace.getRoutingCode());
        decision.setDestinationInstitutionName(institution.getInstitutionName());
        decision.setPublishedEventId(publishedEventId);
        decision.setPublishedRoutingKey(publishedRoutingKey);
        decision.setDecidedAt(now);
        return decision;
    }

    public RoutingError toRoutingError(
            PaymentLineRouting routingTrace,
            UUID publishedEventId,
            String publishedRoutingKey,
            String rejectionCode,
            String rejectionReason,
            OffsetDateTime now) {
        RoutingError error = new RoutingError(UUID.randomUUID());
        error.setLineId(routingTrace.getLineId());
        error.setBatchId(routingTrace.getBatchId());
        error.setSourceEventId(routingTrace.getSourceEventId());
        error.setRejectionCode(rejectionCode);
        error.setRejectionReason(rejectionReason);
        error.setPublishedEventId(publishedEventId);
        error.setPublishedRoutingKey(publishedRoutingKey);
        error.setCreatedAt(now);
        return error;
    }

    private void fillCommonRoutedEvent(
            PaymentLineRoutedOnUsEvent event,
            PaymentLineRouting routingTrace,
            RoutingInstitution institution,
            UUID eventId,
            OffsetDateTime now,
            String eventType) {
        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setOccurredAt(now);
        event.setBatchId(routingTrace.getBatchId());
        event.setLineId(routingTrace.getLineId());
        event.setCorrelationId(routingTrace.getCorrelationId());
        event.setSourceService(SOURCE_SERVICE);
        event.setSequenceNumber(routingTrace.getSequenceNumber());
        event.setCompanyRuc(routingTrace.getCompanyRuc());
        event.setSourceAccountNumber(routingTrace.getSourceAccountNumber());
        event.setCoreFundingId(routingTrace.getCoreFundingId());
        event.setBeneficiaryIdentification(routingTrace.getBeneficiaryIdentification());
        event.setBeneficiaryName(routingTrace.getBeneficiaryName());
        event.setDestinationAccountNumber(routingTrace.getDestinationAccountNumber());
        event.setRoutingCode(routingTrace.getRoutingCode());
        event.setDestinationInstitutionName(institution.getInstitutionName());
        event.setAmount(routingTrace.getAmount());
        event.setCurrency(routingTrace.getCurrency());
        event.setReference(routingTrace.getReference());
        event.setNotificationEmail(routingTrace.getNotificationEmail());
    }
}
