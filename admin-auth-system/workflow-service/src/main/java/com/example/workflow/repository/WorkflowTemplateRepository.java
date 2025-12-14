package com.example.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.workflow.entity.WorkflowTemplate;
import java.util.UUID;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, UUID> {

}
