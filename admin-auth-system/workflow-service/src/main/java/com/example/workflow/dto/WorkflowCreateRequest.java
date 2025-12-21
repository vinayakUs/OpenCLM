package com.example.workflow.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class WorkflowCreateRequest {
    private String name;
    private String description;
    private UUID templateFileId;
    private WorkflowStatus currentStatus;
    private VariableCreateRequest[] variables;

}
