package com.banquito.switchpagos.routing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange batchExchange(@Value("${rabbit.exchange.batch}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public TopicExchange routingExchange(@Value("${rabbit.exchange.routing}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue routingPaymentLinesQueue(@Value("${rabbit.queue.routing.payment-lines}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue settlementOnUsQueue(@Value("${rabbit.queue.settlement.on-us}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue clearingOffUsQueue(@Value("${rabbit.queue.clearing.off-us}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue routingRejectedQueue(@Value("${rabbit.queue.routing.rejected}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding paymentLineRequestedBinding(
            TopicExchange batchExchange,
            Queue routingPaymentLinesQueue,
            @Value("${rabbit.routing-key.payment-line-requested}") String routingKey) {
        return BindingBuilder.bind(routingPaymentLinesQueue).to(batchExchange).with(routingKey);
    }

    @Bean
    public Binding routedOnUsBinding(
            TopicExchange routingExchange,
            Queue settlementOnUsQueue,
            @Value("${rabbit.routing-key.routed-on-us}") String routingKey) {
        return BindingBuilder.bind(settlementOnUsQueue).to(routingExchange).with(routingKey);
    }

    @Bean
    public Binding routedOffUsBinding(
            TopicExchange routingExchange,
            Queue clearingOffUsQueue,
            @Value("${rabbit.routing-key.routed-off-us}") String routingKey) {
        return BindingBuilder.bind(clearingOffUsQueue).to(routingExchange).with(routingKey);
    }

    @Bean
    public Binding lineRejectedBinding(
            TopicExchange routingExchange,
            Queue routingRejectedQueue,
            @Value("${rabbit.routing-key.line-rejected}") String routingKey) {
        return BindingBuilder.bind(routingRejectedQueue).to(routingExchange).with(routingKey);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        JacksonJsonMessageConverter messageConverter = new JacksonJsonMessageConverter();
        messageConverter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return messageConverter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
