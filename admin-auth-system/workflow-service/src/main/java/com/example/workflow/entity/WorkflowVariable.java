package com.example.workflow.entity;

import com.example.common.dto.VariableDataType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "workflow_variable")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class WorkflowVariable extends AuditableEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @NonNull
    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId; // References WorkflowTemplate.id

    @NonNull
    @Column(name = "variable_name", nullable = false)
    private String variableName; // e.g. party_name

    @NonNull
    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VariableDataType dataType; // STRING, DATE, Int

    @Column(name = "label")
    @NonNull
    private String label; // Friendly UI label

    @Column(name = "required")
    private Boolean required = false;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /*
     * ======================
     * Domain behavior
     * ======================
     */

    public void markRequired() {
        this.required = true;
    }

    public void markOptional() {
        this.required = false;
    }

    public void changeDefaultValue(String value) {
        this.defaultValue = value;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}