package com.example.bff.domain.workflow.service;

import com.example.bff.domain.workflow.client.WorkflowClient;
import com.example.bff.domain.workflow.dto.*;
import com.example.common.dto.WorkflowResponse;
import com.example.common.dto.PageResponse;
import com.example.common.dto.WorkflowStatus;
import com.example.bff.domain.document.service.FileUploadService;
import com.example.bff.domain.document.dto.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class WorkflowService implements IWorkflowService {

    private final FileUploadService fileUploadService;
    private final WorkflowClient workflowClient;

    public WorkflowService(FileUploadService fileUploadService, WorkflowClient workflowClient) {
        this.fileUploadService = fileUploadService;
        this.workflowClient = workflowClient;
    }

    public UUID saveWorkflowState(MultipartFile multipartFile, WorkflowCreateRequest workflowCreateRequest) {

        FileUploadResponse fileUploadResponse = fileUploadService.uploadDocument(multipartFile);
        log.info(fileUploadResponse.getFileId().toString());

        UUID workflowUUID = createWorkflowEntity(fileUploadResponse.getFileId(), workflowCreateRequest);
        log.info(workflowUUID.toString());

        return workflowUUID;

    }

    private UUID createWorkflowEntity(UUID template_UUID,
            WorkflowCreateRequest workflowCreateRequest) {
        WorkflowClientRequest req = new WorkflowClientRequest();

        req.setName(workflowCreateRequest.getName());
        req.setDescription(workflowCreateRequest.getDescription());
        req.setTemplateFileId(template_UUID);
        req.setCurrentStatus(workflowCreateRequest.getCurrentStatus());
        req.setVariables(workflowCreateRequest.getVariables());
        return workflowClient.postWorkflow(req);

    }

    public Mono<PageResponse<WorkflowResponse>> getAllWorkflows(String search , int page, int size) {
        return workflowClient.getAllWorkflowv2(search , page, size);
    }

}
