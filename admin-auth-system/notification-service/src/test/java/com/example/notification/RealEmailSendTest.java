// package com.example.notification;

// import java.util.UUID;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.example.notification.entity.Notification;
// import com.example.notification.enums.NotificationChannelType;
// import
// com.example.notification_service.model.notification.NotificationStrategy;
// import com.example.notification.service.NotificationContext;

// @SpringBootTest
// public class RealEmailSendTest {

// @Autowired
// private NotificationContext notificationContext;

// @Test
// void sendTestEmail() {
// Notification notification = Notification.builder()
// .id(UUID.randomUUID())
// .recipient("radhikaandrew12@gmail.com")
// .title("Test Email from Notification Service")
// .message("<h1>Hello!</h1><p>This is a test email sent from the Notification
// Service.</p>")
// .channel(NotificationChannelType.EMAIL)
// .build();

// NotificationStrategy strategy =
// notificationContext.getStrategy(NotificationChannelType.EMAIL);
// strategy.send(notification);
// }
// }


