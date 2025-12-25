package com.example.bff.exception;

public class WorkflowRetrivalException  extends RuntimeException{

    public WorkflowRetrivalException(String message){
        super(message);
    }
    public WorkflowRetrivalException(String message,Throwable throwable){
        super(message,throwable);
    }

}

