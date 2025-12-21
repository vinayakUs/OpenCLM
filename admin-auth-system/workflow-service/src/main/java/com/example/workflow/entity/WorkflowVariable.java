package com.example.workflow.entity;


import com.example.workflow.dto.VariableDataType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_variable")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowVariable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;  // References WorkflowTemplate.id

    @Column(name = "variable_name", nullable = false)
    private String variableName;   // e.g. party_name

    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VariableDataType dataType;       // STRING, DATE, Int

    @Column(name = "label")
    private String label;          // Friendly UI label

    @Column(name = "required")
    private Boolean required = false;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}