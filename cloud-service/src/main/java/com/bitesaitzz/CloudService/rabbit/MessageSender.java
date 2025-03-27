package com.bitesaitzz.CloudService.rabbit;


import com.bitesaitzz.CloudService.models.Message;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private Logger logger;
    @Value("${queue.name}")
    private String queueName;

    public void send(Message message) {
        logger.info("Sending message: " + message.getText());
        rabbitTemplate.convertAndSend(queueName, message);
    }

}
