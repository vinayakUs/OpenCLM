package com.example.workflow.repository;

import com.example.workflow.specification.WorkflowSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.workflow.entity.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, UUID> , JpaSpecificationExecutor<WorkflowTemplate> {


    Page<WorkflowTemplate> findAllByNameContainingIgnoreCase(String search, Pageable pageable);

}
