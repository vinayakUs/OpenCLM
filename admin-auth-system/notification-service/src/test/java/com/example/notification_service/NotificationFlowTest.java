package com.example.notification_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.model.EventType;
import com.example.notification_service.model.NotificationChannelType;
import com.example.notification_service.model.ReferenceType;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.service.NotificationService;

import jakarta.mail.internet.MimeMessage;

@SpringBootTest
public class NotificationFlowTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void testOtpEmailFlow() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .eventType(EventType.valueOf("OTP_SENT"))
                .channel(NotificationChannelType.EMAIL)
                .recipient("test-otp@example.com")
                .variables(Map.of("otp", "123456", "expiryMinutes", 5))
                .referenceType(ReferenceType.AUTH)
                .referenceId(UUID.randomUUID())
                .build();

        // Mock mail sender
        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);
        org.mockito.Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        notificationService.sendNotification(request);

        // Assert
        // 1. Verify Email Sent
        verify(javaMailSender).send(any(MimeMessage.class));

        // 2. Verify DB persistence
        Notification notification = notificationRepository.findAll().stream()
                .filter(n -> "test-otp@example.com".equals(n.getRecipient()))
                .findFirst()
                .orElseThrow();

        assertThat(notification.getTitle()).isEqualTo("Your Login OTP");
        assertThat(notification.getMessage()).contains("123456");
        assertThat(notification.isShowInBell()).isFalse();
    }

    @Test
    void testWorkflowInAppFlow() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationRequest request = NotificationRequest.builder()
                .eventType(EventType.valueOf("WORKFLOW_CREATED"))
                .channel(NotificationChannelType.IN_APP)
                .userId(userId)
                .variables(Map.of("workflowName", "NDA Contract"))
                .referenceType(ReferenceType.WORKFLOW)
                .referenceId(UUID.randomUUID())
                .build();

        // Act
        notificationService.sendNotification(request);

        // Assert
        // Verify DB persistence
        Notification notification = notificationRepository.findAll().stream()
                .filter(n -> userId.equals(n.getUserId()))
                .findFirst()
                .orElseThrow();

        assertThat(notification.getTitle()).isEqualTo("Workflow Created");
        assertThat(notification.getMessage()).contains("NDA Contract");
        assertThat(notification.isShowInBell()).isTrue();
    }
}
