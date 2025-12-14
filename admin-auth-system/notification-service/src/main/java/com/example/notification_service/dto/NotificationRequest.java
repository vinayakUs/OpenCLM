package com.example.notification_service.dto;

import java.util.Map;
import java.util.UUID;

import com.example.notification_service.model.EventType;
import com.example.notification_service.model.NotificationChannelType;
import com.example.notification_service.model.ReferenceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    private EventType eventType;
    private NotificationChannelType channel;

    // Optional: Only for IN_APP
    private UUID userId;

    // Optional: For EMAIL/SMS (if different from user's default)
    private String recipient;

    // Dynamic variables for template replacement
    private Map<String, Object> variables;

    private ReferenceType referenceType;
    private UUID referenceId;
}
