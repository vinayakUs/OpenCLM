package com.example.workflow.repository;

import com.example.workflow.entity.WorkflowFormField;
import com.example.workflow.entity.WorkflowVariable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowVariableRepository extends JpaRepository<WorkflowVariable, UUID> {

     List<WorkflowVariable> findAllByWorkflowId(UUID workFlowId);
}
