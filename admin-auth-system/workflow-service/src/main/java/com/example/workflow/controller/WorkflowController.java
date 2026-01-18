package com.example.workflow.controller;

import com.example.workflow.dto.*;
import com.example.common.dto.ApiResponse;
import com.example.common.dto.PageResponse;
import com.example.common.dto.WorkflowResponse;
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

    @GetMapping()
    public ApiResponse<PageResponse<WorkflowResponse>> getAllWorkflow(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(workflowService.getAllWorkflow(search,page, size));
    }

    @PutMapping("/{id}")
    public ApiResponse<UUID> updateWorkflowTemplate(@PathVariable UUID id,
            @RequestBody WorkflowUpdateRequest dto) {
        UUID uuid = workflowService.updateWorkflowTemplate(id, dto);
        return ApiResponse.success(uuid);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UUID> postWorkflowTemplate(@RequestBody WorkflowCreateRequest dto) {
        UUID uuid = workflowService.postWorkflowTemplate(dto);
        System.out.println("success : " + uuid);
        return ApiResponse.success(uuid);

    }
}
