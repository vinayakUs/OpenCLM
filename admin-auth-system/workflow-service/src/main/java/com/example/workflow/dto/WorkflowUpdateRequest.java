package com.example.workflow.dto;

import lombok.Data;
import com.example.common.dto.WorkflowStatus;

@Data
public class WorkflowUpdateRequest {
    private String name;
    private String description;
    private WorkflowStatus currentStatus;
}
