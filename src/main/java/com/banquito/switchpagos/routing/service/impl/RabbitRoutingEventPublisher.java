package com.banquito.switchpagos.routing.service.impl;

import com.banquito.switchpagos.routing.service.RoutingEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitRoutingEventPublisher implements RoutingEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String routingExchange;
    private final String routedOnUsRoutingKey;
    private final String routedOffUsRoutingKey;
    private final String lineRejectedRoutingKey;

    public RabbitRoutingEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbit.exchange.routing}") String routingExchange,
            @Value("${rabbit.routing-key.routed-on-us}") String routedOnUsRoutingKey,
            @Value("${rabbit.routing-key.routed-off-us}") String routedOffUsRoutingKey,
            @Value("${rabbit.routing-key.line-rejected}") String lineRejectedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.routingExchange = routingExchange;
        this.routedOnUsRoutingKey = routedOnUsRoutingKey;
        this.routedOffUsRoutingKey = routedOffUsRoutingKey;
        this.lineRejectedRoutingKey = lineRejectedRoutingKey;
    }

    @Override
    public void publishOnUs(Object event) {
        rabbitTemplate.convertAndSend(routingExchange, routedOnUsRoutingKey, event);
    }

    @Override
    public void publishOffUs(Object event) {
        rabbitTemplate.convertAndSend(routingExchange, routedOffUsRoutingKey, event);
    }

    @Override
    public void publishRejected(Object event) {
        rabbitTemplate.convertAndSend(routingExchange, lineRejectedRoutingKey, event);
    }
}
