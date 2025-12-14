package com.example.notification_service.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.entity.NotificationTemplate;
import com.example.notification_service.model.NotificationChannelType;
import com.example.notification_service.model.NotificationStatus;
import com.example.notification_service.model.notification.NotificationStrategy;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.repository.NotificationTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationContext notificationContext;
    private final TemplateService templateService;

    public void sendNotification(NotificationRequest request) {
        log.info("Processing notification request: event={}, channel={}", request.getEventType(), request.getChannel());

        // 1. Fetch Template
        NotificationTemplate template = templateRepository
                .findByEventTypeAndChannel(request.getEventType(), request.getChannel())
                .orElseThrow(() -> new IllegalArgumentException("Template not found for event: "
                        + request.getEventType() + " and channel: " + request.getChannel()));

        // 2. Render Content
        String title = templateService.render(template.getTitleTemplate(), request.getVariables());
        String body = templateService.render(template.getBodyTemplate(), request.getVariables());

        // 3. Determine 'showInBell'
        // Rule: Only IN_APP shows in bell. OTP/EMAIL/SMS never show in bell.
        // We can refine this logic further if needed, but for now enforcing channel
        // check.
        boolean showInBell = request.getChannel() == NotificationChannelType.IN_APP;

        // 4. Create & Persist Notification
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .title(title)
                .message(body)
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .channel(request.getChannel())
                .status(NotificationStatus.PENDING)
                .showInBell(showInBell)
                .read(false)
                .build();

        notification = notificationRepository.save(notification);

        // 5. Send via Strategy
        try {
            NotificationStrategy strategy = notificationContext.getStrategy(request.getChannel());
            if (strategy != null) {
                strategy.send(notification);
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(Instant.now());
            } else {
                log.warn("No strategy found for channel: {}", request.getChannel());
                // For IN_APP, we might not have a "sender" strategy if it's just DB
                // persistence.
                // But if we do want real-time WS updates, we'd have a strategy.
                // For now, assuming successful persistence IS the 'send' for IN_APP if no
                // strategy exists.
                if (request.getChannel() == NotificationChannelType.IN_APP) {
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(Instant.now());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send notification via strategy", e);
            notification.setStatus(NotificationStatus.FAILED);
        }

        notificationRepository.save(notification);
    }
}