package com.example.bff.domain.workflow.dto;

import lombok.Data;
import java.util.UUID;
import com.example.common.dto.WorkflowStatus;

@Data
public class WorkflowClientRequest {
    private String name;
    private String description;
    private UUID templateFileId;
    private WorkflowStatus currentStatus;
    private WorkFlowVariables[] variables;
}


