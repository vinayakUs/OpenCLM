package com.example.bff.domain.workflow.dto;

import lombok.Data;
import java.util.UUID;
import com.example.common.dto.WorkflowStatus;

@Data
public class WorkflowCreateRequest {

    private String name;
    private String description;
    private WorkflowStatus currentStatus;
    private WorkFlowVariables[] variables;
}


