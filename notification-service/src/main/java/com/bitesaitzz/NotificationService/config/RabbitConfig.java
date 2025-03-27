package com.bitesaitzz.NotificationService.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        SimpleMessageConverter messageConverter = new SimpleMessageConverter();
        messageConverter.setAllowedListPatterns(List.of(new String[]{"com.bitesaitzz.NotificationService.models.*", "java.lang.Enum"}));
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}

