package com.example.bff.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class WorkflowClientRequest {
    private String name;
    private String description;
    private UUID templateFileId;
    private WorkflowStatus currentStatus;
    private WorkFlowVariables[] variables;
}
