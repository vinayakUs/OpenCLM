package com.example.bff.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class WorkflowCreateRequest {
    private String name;
    private String description;
    private WorkflowStatus current_status;
    private WorkFlowVariables[] variables;
}





