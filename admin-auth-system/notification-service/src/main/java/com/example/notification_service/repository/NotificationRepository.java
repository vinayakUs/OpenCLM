package com.example.notification_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.notification_service.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {


    // List<Notification> findByUserIdAndChannelOrderByCreatedAtDesc(
    // UUID userId,
    // NotificationChannelType channel
    // );

    // long countByUserIdAndChannelAndReadFalse(
    // UUID userId,
    // NotificationChannelType channel
    // );
}
