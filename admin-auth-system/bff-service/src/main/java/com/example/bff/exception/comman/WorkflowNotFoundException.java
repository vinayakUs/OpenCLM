package com.example.bff.exception.comman;

// For 404 Data missing
public class WorkflowNotFoundException extends DownstreamException {
    public WorkflowNotFoundException(String id,Throwable cause) {
        super("Workflow " + id + " not found", "DATA_NOT_FOUND", cause);
    }
}