package com.example.contract.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OutboxEvent {

    @Id
    @GeneratedValue
    private UUID id;

    private String aggregateType;
    private UUID aggregateId;

    private String eventType;
    private UUID eventId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> payload;

    @Column(name = "created_at" )
    @CreatedDate
    private OffsetDateTime createdAt;
    private String status;
    private int retryCount = 0 ;
    private OffsetDateTime lastAttemptAt;
}
