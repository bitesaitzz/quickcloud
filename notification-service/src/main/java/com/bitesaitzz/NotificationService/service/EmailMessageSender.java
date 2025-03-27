package com.bitesaitzz.NotificationService.service;


import com.bitesaitzz.NotificationService.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailMessageSender implements MessageSender {


    public EmailMessageSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMessage(Message messageToSend) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(messageToSend.getMail());
        message.setSubject(messageToSend.getSubject());
        message.setText(messageToSend.getText());
        javaMailSender.send(message);
    }
}
