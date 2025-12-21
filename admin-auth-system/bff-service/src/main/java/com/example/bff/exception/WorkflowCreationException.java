package com.example.bff.exception;

public class WorkflowCreationException extends RuntimeException{

    public WorkflowCreationException(String message){
        super(message);
    }
    public WorkflowCreationException(String message,Throwable throwable){
        super(message,throwable);
    }
}
