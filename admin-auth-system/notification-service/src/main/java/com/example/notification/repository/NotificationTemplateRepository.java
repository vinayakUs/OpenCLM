package com.example.notification.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.notification.entity.NotificationTemplate;
import com.example.notification.enums.EventType;
import com.example.notification.enums.NotificationChannelType;

public interface NotificationTemplateRepository
        extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByEventTypeAndChannel(
            EventType eventType,
            NotificationChannelType channel);
}



