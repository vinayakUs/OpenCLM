package com.example.workflow.exception;

import java.util.UUID;

public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(UUID id) {
        super("Workflow not found with id: " + id);
    }
}
