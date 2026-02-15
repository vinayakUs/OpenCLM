package com.example.bff.exception.comman;


import lombok.Getter;

@Getter
public class DownstreamException extends RuntimeException {
    private final String errorCode;

    public DownstreamException( String message , String errorCode , Throwable cause) {
        super(message,cause);
        this.errorCode = errorCode;
    }

}
