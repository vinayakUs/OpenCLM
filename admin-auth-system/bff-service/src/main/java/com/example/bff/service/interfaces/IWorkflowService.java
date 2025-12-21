package com.example.bff.service.interfaces;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import com.example.bff.dto.WorkflowCreateRequest;
import reactor.core.publisher.Mono;

public interface IWorkflowService {

    public UUID saveWorkflowState(MultipartFile multipartFile, WorkflowCreateRequest workflowCreateRequest);

}
