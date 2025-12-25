package com.example.bff.domain.workflow.controller;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.PageResponse;
import com.example.bff.domain.workflow.dto.WorkflowCreateRequest;
import com.example.common.dto.WorkflowResponse;
import com.example.bff.domain.workflow.service.IWorkflowService;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@Slf4j
@RequiredArgsConstructor
public class WorkflowController {

    private final IWorkflowService workflowService;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // @GetMapping("/{id}")
    // public void getWorkflow(){
    // return null;
    // }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> postWorkflow(
            @org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient("bff-client") org.springframework.security.oauth2.client.OAuth2AuthorizedClient authorizedClient,
            @RequestPart("file") MultipartFile file,
            @Validated @RequestPart("data") WorkflowCreateRequest workflowCreateRequest

    ) {
        log.info("Received request to save workflow: {}", workflowCreateRequest.getName());
        return ResponseEntity.ok()
                .body(ApiResponse.success(workflowService.saveWorkflowState(file, workflowCreateRequest)));
    }

    @GetMapping(value = "/")
    public ResponseEntity<ApiResponse<PageResponse<WorkflowResponse>>> getAllWorkflows(
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(ApiResponse.success(workflowService.getAllWorkflows(page, size)));
    }

}
