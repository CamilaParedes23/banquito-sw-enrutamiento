package com.banquito.switchpagos.routing.listener;

import com.banquito.switchpagos.routing.dto.event.PaymentLineRequestedEvent;
import com.banquito.switchpagos.routing.service.RoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentLineRequestedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentLineRequestedListener.class);

    private final RoutingService routingService;

    public PaymentLineRequestedListener(RoutingService routingService) {
        this.routingService = routingService;
    }

    @RabbitListener(queues = "${rabbit.queue.routing.payment-lines}")
    public void onPaymentLineRequested(PaymentLineRequestedEvent event) {
        if (event == null) {
            LOGGER.warn("Evento nulo recibido por routing-service");
            return;
        }
        LOGGER.info("Evento recibido para enrutamiento. batchId={}, lineId={}", event.getBatchId(), event.getLineId());
        routingService.routePaymentLine(event);
    }
}
