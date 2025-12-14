package com.example.workflow.repository;

import com.example.workflow.entity.WorkflowFormField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowVariableRepository extends JpaRepository<WorkflowFormField, UUID> {
}
