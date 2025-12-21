package com.example.bff.service;

import com.example.bff.client.WorkflowClient;
import com.example.bff.dto.*;
import com.example.bff.service.interfaces.IWorkflowService;
import io.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class WorkflowService implements IWorkflowService {

    private final FileUploadService fileUploadService;
    private final WorkflowClient workflowClient;

    public WorkflowService(FileUploadService fileUploadService, WorkflowClient workflowClient) {
        this.fileUploadService = fileUploadService;
        this.workflowClient = workflowClient;
    }

    public UUID saveWorkflowState(MultipartFile multipartFile, WorkflowCreateRequest workflowCreateRequest) {

        FileUploadResponse fileUploadResponse = fileUploadService.uploadDocument(multipartFile);

        WorkflowResponse workflowResponse = createWorkflowEntity(fileUploadResponse.getFileId() , workflowCreateRequest);

        return workflowResponse.getId();



    }

    private WorkflowResponse createWorkflowEntity(UUID template_UUID,
            WorkflowCreateRequest workflowCreateRequest) {
        WorkflowClientRequest req = new WorkflowClientRequest();

        req.setName(workflowCreateRequest.getName());
        req.setDescription(workflowCreateRequest.getDescription());
        req.setTemplateFileId(template_UUID);
        req.setCurrentStatus(WorkflowStatus.DRAFT);
        req.setVariables(workflowCreateRequest.getVariables());
        System.out.println("logging before sending workflow : " + req.toString());
        return workflowClient.postWorkflow(req);

    }

}
