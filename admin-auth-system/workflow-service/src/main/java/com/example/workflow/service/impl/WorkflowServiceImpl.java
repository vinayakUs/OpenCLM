package com.example.workflow.service.impl;

import com.example.workflow.dto.*;
import com.example.common.dto.WorkflowResponse;
import com.example.common.dto.VariableResponse;
import com.example.common.dto.PageResponse;
import com.example.common.dto.WorkflowStatus;
import com.example.workflow.entity.WorkflowTemplate;
import com.example.workflow.entity.WorkflowTemplate_;
import com.example.workflow.entity.WorkflowVariable;
import com.example.workflow.exception.WorkflowNotFoundException;
import com.example.workflow.repository.WorkflowTemplateRepository;
import com.example.workflow.repository.WorkflowVariableRepository;
import com.example.workflow.service.WorkflowService;
import com.example.workflow.specification.WorkflowSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowTemplateRepository workflowTemplateRepository;

    private final WorkflowVariableRepository workflowVariableRepository;

    public WorkflowServiceImpl(WorkflowTemplateRepository workflowTemplateRepository,
            WorkflowVariableRepository workflowVariableRepository) {
        this.workflowTemplateRepository = workflowTemplateRepository;
        this.workflowVariableRepository = workflowVariableRepository;
    }

    @Override
    public WorkflowResponse getWorkflowTemplate(UUID id) {
        WorkflowTemplate workflowTemplate = workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));
        List<WorkflowVariable> workflowVariable = workflowVariableRepository
                .findAllByWorkflowId(workflowTemplate.getId());

        WorkflowResponse response = new WorkflowResponse();

        BeanUtils.copyProperties(workflowTemplate, response);

        List<VariableResponse> variableResponses = workflowVariable.stream().map(
                x -> {
                    VariableResponse r = new VariableResponse();
                    BeanUtils.copyProperties(x, r);
                    return r;
                }).toList();

        response.setVariableResponse(variableResponses);
        return response;
    }

    @Override
    public UUID updateWorkflowTemplate(UUID id, WorkflowUpdateRequest dto) {
        WorkflowTemplate existingTemplate = workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow Template not found"));

        // if (dto.getName() != null)
        // existingTemplate.setName(dto.getName());
        // if (dto.getDescription() != null)
        // existingTemplate.setDescription(dto.getDescription());
        // if (dto.getCurrentStatus() != null)
        // existingTemplate.setCurrentStatus(dto.getCurrentStatus());

        // Save
        WorkflowTemplate saved = workflowTemplateRepository.save(existingTemplate);

        return saved.getId();
    }

    @Override
    public UUID postWorkflowTemplate(WorkflowCreateRequest dto) {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate(
                dto.getName(),
                dto.getDescription(),
                dto.getTemplateFileId());
        // change status to publish
        if (dto.getCurrentStatus() == WorkflowStatus.PUBLISHED) {
            workflowTemplate.publish();
        }

        WorkflowTemplate saved = workflowTemplateRepository.save(workflowTemplate);
        log.info(String.valueOf(saved.getId()));

        List<WorkflowVariable> variables = Arrays.stream(dto.getVariables()).map(
                x -> new WorkflowVariable(
                        saved.getId(),
                        x.getVariableName(),
                        x.getDataType(),
                        x.getLabel())

        ).toList();
        workflowVariableRepository.saveAll(variables);

        return saved.getId();
    }

    @Override
    public PageResponse<WorkflowResponse> getAllWorkflow(String search, int page, int size, String sortBy,
            String direction) {

        Specification<WorkflowTemplate> specification = Specification.where(
                WorkflowSpecification.filterName(search));

        Map<String, JpaSort> SORTS = Map.of(
                "createdAt", JpaSort.of(WorkflowTemplate_.createdAt),
                "name", JpaSort.of(WorkflowTemplate_.name)

        );

        JpaSort jpaSort = SORTS.getOrDefault(
                sortBy,
                SORTS.get("createdAt"));
        Sort finalSort;
        if ("DESC".equalsIgnoreCase(direction)) {
            finalSort = jpaSort.descending();
        } else {
            finalSort = jpaSort.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, finalSort);

        Page<WorkflowTemplate> response = workflowTemplateRepository.findAll(specification, pageable);

        System.out.println(response.getContent());

        Page<WorkflowResponse> mappedPage = response.map(this::mapToResponse);

        PageResponse<WorkflowResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(mappedPage.getContent());
        pageResponse.setPage(mappedPage.getNumber());
        pageResponse.setSize(mappedPage.getSize());
        pageResponse.setTotalElements(mappedPage.getTotalElements());
        pageResponse.setTotalPages(mappedPage.getTotalPages());
        pageResponse.setLast(mappedPage.isLast());

        return pageResponse;
    }

    private WorkflowResponse mapToResponse(WorkflowTemplate template) {
        WorkflowResponse response = new WorkflowResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setTemplateFileId(template.getTemplateFileId());
        response.setCurrentStatus(template.getCurrentStatus());
        response.setVersion(template.getVersion());
        response.setCreatedBy(template.getCreatedBy());
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        return response;
    }
}
