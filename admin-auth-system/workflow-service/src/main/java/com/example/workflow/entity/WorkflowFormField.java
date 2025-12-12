package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_form_field")
@AllArgsConstructor
@NoArgsConstructor

public class WorkflowFormField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;  // References WorkflowTemplate.id

    @Column(name = "variable_id", nullable = false)
    private UUID variableId;  // References WorkflowVariable.id

    private String label;        // Override label for UI
    private String placeholder;  // Placeholder shown in input field

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "visibility_rule")
    private String visibilityRule;  // Future: Show when condition true

    @Column(name = "validation_rule")
    private String validationRule;  // Regex or constraints

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
