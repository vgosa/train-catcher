package org.group21.trainsearch.service;

import org.group21.trainsearch.model.message.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String destination, Notification message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
