package com.example.notification.service.strategy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.example.notification.entity.Notification;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailNotificationStrategyTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailNotificationStrategy emailNotificationStrategy;

    @Test
    void testSend() {
        // Arrange
        Notification notification = Notification.builder()
                .recipient("test@example.com")
                .title("Test Subject")
                .message("Test Body")
                .build();

        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailNotificationStrategy.send(notification);

        // Assert
        verify(javaMailSender).send(any(MimeMessage.class));
    }
}


