package com.example.workflow.service;

import com.example.workflow.dto.WorkflowCreateRequest;
import com.example.workflow.dto.WorkflowResponse;
import com.example.workflow.dto.WorkflowUpdateRequest;
import java.util.UUID;

public interface WorkflowService {
    WorkflowResponse getWorkflowTemplate(UUID id);

    UUID updateWorkflowTemplate(UUID id, WorkflowUpdateRequest dto);

    UUID postWorkflowTemplate(WorkflowCreateRequest dto,UUID created_by);
}
