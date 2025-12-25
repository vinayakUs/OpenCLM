package com.example.workflow.entity;

import jakarta.persistence.*;
import com.example.common.dto.WorkflowStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class WorkflowTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    public UUID id;

    @Column(name = "name", nullable = false)
    @NonNull
    public String name;

    @Column(name = "description")
    @NonNull
    public String description;

    @NonNull
    @Column(name = "template_file_id", nullable = false)
    public UUID templateFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    public WorkflowStatus currentStatus = WorkflowStatus.DRAFT; // DRAFT or PUBLISHED

    @Column(name = "version")
    public Integer version = 1;

    /*
     * ======================
     * Domain behavior
     * ======================
     */

    public void publish() {
        this.currentStatus = WorkflowStatus.PUBLISHED;
    }

}
