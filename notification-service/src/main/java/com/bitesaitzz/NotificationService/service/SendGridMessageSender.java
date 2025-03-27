package com.bitesaitzz.NotificationService.service;
import com.bitesaitzz.NotificationService.models.Message;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridMessageSender implements MessageSender {
    private final Email from;
    private final SendGrid sendGrid;

    public SendGridMessageSender(@Value("${spring.mail.username}") String email,
                                 @Value("${SENDGRID.API.KEY}") String apiKey) {
        this.from = new Email(email);
        this.sendGrid = new SendGrid(apiKey);
    }

    @Override
    public void sendMessage(Message messageToSend) throws IOException {
        String subject = messageToSend.getSubject();
        Email to = new Email(messageToSend.getMail());
        Content content = new Content("text/plain", messageToSend.getText());
        Mail mail = new Mail(from, subject, to, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }

    }


}
