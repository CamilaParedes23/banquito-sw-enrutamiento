package com.banquito.switchpagos.routing.service;

import com.banquito.switchpagos.routing.dto.event.PaymentLineRequestedEvent;

public interface RoutingService {

    void routePaymentLine(PaymentLineRequestedEvent event);
}
