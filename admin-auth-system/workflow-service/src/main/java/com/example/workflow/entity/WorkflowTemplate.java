package com.example.workflow.entity;

import jakarta.persistence.*;
import com.example.workflow.dto.WorkflowStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_template")
@Data
public class WorkflowTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "template_file_id", nullable = false)
    public UUID templateFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    public WorkflowStatus currentStatus = WorkflowStatus.DRAFT; // DRAFT or PUBLISHED

    @Column(name = "version")
    public Integer version = 1;

    @Column(name = "created_by", nullable = false)
    public UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}
