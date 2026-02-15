package com.example.bff.exception.comman;

public class DownstreamInternalErrorException extends DownstreamException {


    public DownstreamInternalErrorException(String service,Throwable cause) {
        super(service+ " Internal Error", "INTERNAL_SERVER_ERROR", cause);
    }
}
