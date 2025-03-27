package com.bitesaitzz.NotificationService.service;

import com.bitesaitzz.NotificationService.models.Message;

import java.io.IOException;

public interface MessageSender {
    void sendMessage(Message message) throws IOException;
}
