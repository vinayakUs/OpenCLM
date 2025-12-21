package com.example.bff.controller;

import com.example.bff.dto.WorkflowCreateRequest;
import com.example.bff.service.WorkflowService;
import com.example.bff.service.interfaces.IWorkflowService;

import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> postWorkflow(
            @org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient("bff-client") org.springframework.security.oauth2.client.OAuth2AuthorizedClient authorizedClient,
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String workflowCreateRequestJson) {

        WorkflowCreateRequest workflowCreateRequest;
        try {
            workflowCreateRequest = objectMapper.readValue(workflowCreateRequestJson, WorkflowCreateRequest.class);
        } catch (Exception e) {
            throw  new RuntimeException("Invalid JSON format for 'data'", e);
        }

        log.info("Received request to save workflow: {}", workflowCreateRequest.getName());

        return ResponseEntity.ok(workflowService.saveWorkflowState(file, workflowCreateRequest));

    }

}
