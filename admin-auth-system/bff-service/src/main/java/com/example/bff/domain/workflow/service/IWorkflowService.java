package com.example.bff.domain.workflow.service;

import com.example.common.dto.PageResponse;
import com.example.common.dto.WorkflowResponse;
import com.example.bff.domain.workflow.dto.WorkflowCreateRequest;


public interface IWorkflowService {
    java.util.UUID saveWorkflowState(org.springframework.web.multipart.MultipartFile multipartFile,
            WorkflowCreateRequest workflowCreateRequest);

    PageResponse<WorkflowResponse> getAllWorkflows(int page, int size);
}
