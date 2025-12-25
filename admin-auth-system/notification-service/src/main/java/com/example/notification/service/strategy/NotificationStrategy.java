package com.example.notification.service.strategy;

import com.example.notification.entity.Notification;
import com.example.notification.enums.NotificationChannelType;

public interface NotificationStrategy {

    NotificationChannelType channel();

    void send(Notification notification);
}




