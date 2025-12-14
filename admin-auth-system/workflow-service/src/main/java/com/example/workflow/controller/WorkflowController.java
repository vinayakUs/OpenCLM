package com.example.workflow.controller;

import com.example.workflow.dto.ApiResponse;
import com.example.workflow.dto.WorkflowTemplateDTO;
import com.example.workflow.service.WorkflowService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/{id}")
    public ApiResponse<WorkflowTemplateDTO> getWorkflowTemplate(@PathVariable UUID id) {
        WorkflowTemplateDTO dto  = workflowService.getWorkflowTemplate(id);
        return ApiResponse.success(dto);
    }

    @PutMapping("/{id}")
    public  ApiResponse<WorkflowTemplateDTO> updateWorkflowTemplate(@PathVariable UUID id,
            @RequestBody WorkflowTemplateDTO dto) {
        WorkflowTemplateDTO res = workflowService.updateWorkflowTemplate(id, dto);
        return ApiResponse.success(res);
    }
}
