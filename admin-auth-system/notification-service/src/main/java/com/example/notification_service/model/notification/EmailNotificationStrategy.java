package com.example.notification_service.model.notification;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.notification_service.entity.Notification;
import com.example.notification_service.model.NotificationChannelType;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationStrategy implements NotificationStrategy {

    private final JavaMailSender javaMailSender;

    @Override
    public NotificationChannelType channel() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public void send(Notification notification) {
        log.info("Sending email notification to {}", notification.getRecipient());
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getMessage(), true); // true = html

            javaMailSender.send(message);
            log.info("Email sent successfully to {}", notification.getRecipient());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", notification.getRecipient(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
