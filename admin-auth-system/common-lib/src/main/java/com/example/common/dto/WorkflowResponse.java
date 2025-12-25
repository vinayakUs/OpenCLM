package com.example.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID templateFileId;
    private WorkflowStatus currentStatus;
    private Integer version;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<VariableResponse> variableResponse;
}
