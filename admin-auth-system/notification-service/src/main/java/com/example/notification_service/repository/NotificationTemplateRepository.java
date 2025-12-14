package com.example.notification_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.notification_service.entity.NotificationTemplate;
import com.example.notification_service.model.EventType;
import com.example.notification_service.model.NotificationChannelType;

public interface NotificationTemplateRepository
        extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByEventTypeAndChannel(
            EventType eventType,
            NotificationChannelType channel);
}
