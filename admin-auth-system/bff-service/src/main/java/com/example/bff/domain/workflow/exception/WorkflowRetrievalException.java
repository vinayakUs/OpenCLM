package com.example.bff.domain.workflow.exception;

public class WorkflowRetrievalException extends RuntimeException{

    public WorkflowRetrievalException(String message){
        super(message);
    }
    public WorkflowRetrievalException(String message, Throwable throwable){
        super(message,throwable);
    }

}

