package com.example.workflow.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditableEntity {

    @CreatedBy
    @Column(name = "created_by",nullable = false,updatable = false)
    protected UUID createdBy;

    @CreatedDate
    @Column(name = "created_at",nullable = false, updatable = false)
    protected OffsetDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by" ,nullable = false)
    protected UUID updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    protected OffsetDateTime updatedAt;
}
