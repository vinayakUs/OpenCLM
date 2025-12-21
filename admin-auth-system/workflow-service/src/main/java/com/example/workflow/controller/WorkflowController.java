package com.example.workflow.controller;

import com.example.workflow.dto.ApiResponse;
import com.example.workflow.dto.WorkflowCreateRequest;
import com.example.workflow.dto.WorkflowResponse;
import com.example.workflow.dto.WorkflowUpdateRequest;
import com.example.workflow.service.WorkflowService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/{id}")
    public ApiResponse<WorkflowResponse> getWorkflowTemplate(@PathVariable UUID id) {
        WorkflowResponse res = workflowService.getWorkflowTemplate(id);
        return ApiResponse.success(res);
    }

    @PutMapping("/{id}")
    public ApiResponse<UUID> updateWorkflowTemplate(@PathVariable UUID id,
            @RequestBody WorkflowUpdateRequest dto) {
        UUID uuid = workflowService.updateWorkflowTemplate(id, dto);
        return ApiResponse.success(uuid);
    }

    @PostMapping(value = "/" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UUID> postWorkflowTemplate(@RequestBody WorkflowCreateRequest dto , @AuthenticationPrincipal Jwt jwt) {
        System.out.println(dto.toString());
        System.out.println(jwt.getSubject());
        UUID uuid = workflowService.postWorkflowTemplate(dto,UUID.fromString(jwt.getSubject()));
        System.out.println("success : " + uuid);
        return ApiResponse.success(uuid);

    }
}
