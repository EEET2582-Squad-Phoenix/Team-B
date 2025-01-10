package com.teamb.notification.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.notification.kafka.producers.NotificationProducer;

@RestController
public class NotificationController {

    @Autowired
    private NotificationProducer notificationProducer;

    @PostMapping("/send-notification")
    public void sendNotification(@RequestBody String message) {
        notificationProducer.sendNotification("notifications", message);
    }
}
