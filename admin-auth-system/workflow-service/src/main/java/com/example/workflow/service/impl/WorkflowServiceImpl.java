package com.example.workflow.service.impl;

import com.example.workflow.dto.WorkflowTemplateDTO;
import com.example.workflow.entity.WorkflowTemplate;
import com.example.workflow.repository.WorkflowTemplateRepository;
import com.example.workflow.service.WorkflowService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowTemplateRepository workflowTemplateRepository;

    public WorkflowServiceImpl(WorkflowTemplateRepository workflowTemplateRepository) {
        this.workflowTemplateRepository = workflowTemplateRepository;
    }

    @Override
    public WorkflowTemplateDTO getWorkflowTemplate(UUID id) {
        WorkflowTemplate workflowTemplate = workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow Template not found"));
        WorkflowTemplateDTO dto = new WorkflowTemplateDTO();
        BeanUtils.copyProperties(workflowTemplate, dto);
        return dto;
    }

    @Override
    public WorkflowTemplateDTO updateWorkflowTemplate(UUID id, WorkflowTemplateDTO dto) {
        WorkflowTemplate existingTemplate = workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow Template not found"));

        existingTemplate.setName(dto.getName());
        existingTemplate.setDescription(dto.getDescription());
        // Add other fields as necessary, but usually ID, logic fields like
        // template_file_id might require checks
        // user requirement was just "update", assuming basic fields for now
        if (dto.getCurrent_status() != null)
            existingTemplate.setCurrent_status(dto.getCurrent_status());
        if (dto.getVersion() != null)
            existingTemplate.setVersion(dto.getVersion());

        // Save
        WorkflowTemplate saved = workflowTemplateRepository.save(existingTemplate);

        WorkflowTemplateDTO responseDto = new WorkflowTemplateDTO();
        BeanUtils.copyProperties(saved, responseDto);
        return responseDto;
    }
}
