package com.banquito.switchpagos.routing.service;

public interface RoutingEventPublisher {

    void publishOnUs(Object event);

    void publishOffUs(Object event);

    void publishRejected(Object event);
}
