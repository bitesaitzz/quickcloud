package com.bitesaitzz.NotificationService.service;


import com.bitesaitzz.NotificationService.models.Message;
import com.bitesaitzz.NotificationService.models.MessageType;
import com.sendgrid.SendGrid;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitReceiver {

    @Autowired
    Logger logger;
    @Autowired
    private EmailMessageSender emailMessageSender;
    @Autowired
    private TelegramMessageSender telegramMessageSender;
    @Autowired
    private SendGridMessageSender sendGridMessageSender;

    @Value("${queue.name")
    private String queueName;

    private MessageSender messageSender;


    //    @RabbitListener(queues = "#{@queueName}")
    @RabbitListener(queues = "message-queue")
    public void receiveMessage(Message mess) {
        logger.info("Rabbit get: " + mess.toString());
        if(mess.getType() == MessageType.EMAIL){
            messageSender= emailMessageSender;
        }
        else if(mess.getType() == MessageType.TELEGRAM){
            messageSender= telegramMessageSender;
        }
        else if(mess.getType() == MessageType.SENDGRID){
            messageSender= sendGridMessageSender;
        }
        if (messageSender != null) {
            try {
                messageSender.sendMessage(mess);
                logger.info("Mail was sent. \nData: " + mess);
            } catch (Exception e) {
                logger.error("Error, while sending message: " + e.getMessage());
            }
        } else {
            logger.error("No message sender found for type: " + mess.getType());
        }


    }
}
