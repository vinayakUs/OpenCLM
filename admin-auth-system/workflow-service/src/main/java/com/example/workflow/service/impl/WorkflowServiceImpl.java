package com.example.workflow.service.impl;

import com.example.workflow.dto.VariableResponse;
import com.example.workflow.dto.WorkflowCreateRequest;
import com.example.workflow.dto.WorkflowResponse;
import com.example.workflow.dto.WorkflowUpdateRequest;
import com.example.workflow.entity.WorkflowTemplate;
import com.example.workflow.entity.WorkflowVariable;
import com.example.workflow.exception.WorkflowNotFoundException;
import com.example.workflow.repository.WorkflowTemplateRepository;
import com.example.workflow.repository.WorkflowVariableRepository;
import com.example.workflow.service.WorkflowService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowTemplateRepository workflowTemplateRepository;

    private final WorkflowVariableRepository workflowVariableRepository;

    public WorkflowServiceImpl(WorkflowTemplateRepository workflowTemplateRepository, WorkflowVariableRepository workflowVariableRepository) {
        this.workflowTemplateRepository = workflowTemplateRepository;
        this.workflowVariableRepository = workflowVariableRepository;
    }

    @Override
    public WorkflowResponse getWorkflowTemplate(UUID id) {
        WorkflowTemplate workflowTemplate = workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id)
                );
        List<WorkflowVariable> workflowVariable = workflowVariableRepository.findAllByWorkflowId(workflowTemplate.getId());


        WorkflowResponse response = new WorkflowResponse();

        BeanUtils.copyProperties(workflowTemplate, response);

        List<VariableResponse> variableResponses = workflowVariable.stream().map(
                x->{
                    VariableResponse r = new VariableResponse();
                    BeanUtils.copyProperties(x,r);
                    return r;
                }
        ).toList();

        response.setVariableResponse(variableResponses);
        return response;
    }

    @Override
    public UUID updateWorkflowTemplate(UUID id, WorkflowUpdateRequest dto) {
        WorkflowTemplate existingTemplate = workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow Template not found"));

        if (dto.getName() != null)
            existingTemplate.setName(dto.getName());
        if (dto.getDescription() != null)
            existingTemplate.setDescription(dto.getDescription());
        if (dto.getCurrentStatus() != null)
            existingTemplate.setCurrentStatus(dto.getCurrentStatus());

        // Save
        WorkflowTemplate saved = workflowTemplateRepository.save(existingTemplate);


        return saved.getId();
    }

    @Override
    public UUID postWorkflowTemplate(WorkflowCreateRequest dto,UUID created_by) {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.setName(dto.getName());
        workflowTemplate.setDescription(dto.getDescription());
        workflowTemplate.setTemplateFileId(dto.getTemplateFileId());
        workflowTemplate.setCurrentStatus(dto.getCurrentStatus());
        workflowTemplate.setCreatedBy(created_by);

        WorkflowTemplate saved = workflowTemplateRepository.save(workflowTemplate);

        List<WorkflowVariable> variables = Arrays.stream(dto.getVariables()).map(
                x-> WorkflowVariable.builder()
                        .workflowId(saved.getId())
                        .variableName(x.getVariableName())
                        .dataType(x.getDataType())
                        .label(x.getLabel())
                        .required(x.getRequired())
                        .defaultValue(x.getDefaultValue())
                        .sortOrder(x.getSortValue())
                        .build()
        ).toList();

        workflowVariableRepository.saveAll(variables);
        return saved.getId();
    }
}
