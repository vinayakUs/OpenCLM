package com.example.workflow.service;

import com.example.workflow.dto.WorkflowTemplateDTO;
import java.util.UUID;

public interface WorkflowService {
    WorkflowTemplateDTO getWorkflowTemplate(UUID id);

    WorkflowTemplateDTO updateWorkflowTemplate(UUID id, WorkflowTemplateDTO dto);
}
