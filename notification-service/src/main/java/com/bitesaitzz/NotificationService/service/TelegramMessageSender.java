package com.bitesaitzz.NotificationService.service;


import com.bitesaitzz.NotificationService.models.Message;
import org.slf4j.Logger;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class TelegramMessageSender implements MessageSender {
    private final Logger logger;

    public TelegramMessageSender(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void sendMessage(Message message) {
        logger.info("sending using telegram...");
    }
}
