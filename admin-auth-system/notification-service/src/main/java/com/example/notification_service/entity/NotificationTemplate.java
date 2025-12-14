package com.example.notification_service.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.notification_service.model.EventType;
import com.example.notification_service.model.NotificationChannelType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification_template", uniqueConstraints = @UniqueConstraint(columnNames = { "event_type", "channel" }))

public class NotificationTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannelType channel;

    @Column(name = "title_template")
    private String titleTemplate;

    @Column(name = "body_template", nullable = false)
    private String bodyTemplate;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
