package com.example.bff.domain.workflow.exception;

public class WorkflowCreationException extends RuntimeException{

    public WorkflowCreationException(String message,Throwable throwable){
        super(message,throwable);
    }
}

