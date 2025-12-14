package com.example.workflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WorkflowTemplateDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID template_file_id;
    private String current_status;
    private Integer version;
    private UUID created_by;
    private LocalDateTime createdAt;
    private LocalDateTime updated_at;
}
