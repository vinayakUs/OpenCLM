package com.example.workflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class WorkflowResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID template_file_id;
    private WorkflowStatus current_status;
    private Integer version;
    private UUID created_by;
    private LocalDateTime createdAt;
    private LocalDateTime updated_at;
    private List<VariableResponse> variableResponse;
}
