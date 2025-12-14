package com.example.notification_service.model.notification;

import com.example.notification_service.entity.Notification;
import com.example.notification_service.model.NotificationChannelType;

public interface NotificationStrategy {

    NotificationChannelType channel();

    void send(Notification notification);
}
