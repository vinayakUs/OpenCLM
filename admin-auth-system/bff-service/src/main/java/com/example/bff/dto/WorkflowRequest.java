package com.example.bff.dto;

import java.time.LocalDateTime;
import java.util.UUID;

enum WorkflowStatus {
    DRAFT,
    PUBLISHED
}

public class WorkflowRequest {
    private UUID id;
    private String name;
    private String description;
    private UUID template_file_id;
    private String current_status;
    private Integer version;
    private UUID created_by;
    private LocalDateTime createdAt;
    private LocalDateTime updated_at;
    private WorkflowStatus  workflow_status;

}
