package com.bitesaitzz.CloudService.services;


import com.bitesaitzz.CloudService.models.Message;
import com.bitesaitzz.CloudService.models.MessageType;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private Logger logger;
    @Value("${queue.name}")
    private String queueName;

    public void send(Message message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }

    public void createAndSendMessage(String code, String user, MessageType type){
        Message message = Message.builder()
                .mail(user)
                .subject("Verification code")
                .text("Your verification code is: " + code +
                        "<br>Link to the cloud: <a href=\"https://quickcloud.xyz/code/" + code + "\">LINK</a>")
                .type(type)
                .build();
        logger.info("Sending message: " + message);
        send(message);


    }
}
