package com.teamb.notification.kafka.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void listenNotification(String message) {
        System.out.println("Received notification: " + message);
        // Process the notification, e.g., send it to clients or log it
    }
}
