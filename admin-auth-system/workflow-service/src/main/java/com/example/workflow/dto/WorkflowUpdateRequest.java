package com.example.workflow.dto;

import lombok.Data;

@Data
public class WorkflowUpdateRequest {
    private String name;
    private String description;
    private WorkflowStatus currentStatus;
}
