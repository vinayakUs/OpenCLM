package com.example.bff.exception.comman;

public class DownstreamTimeoutException extends DownstreamException {


    public DownstreamTimeoutException(String service,Throwable cause) {
        super(service+ " Timed Out", "SERVICE_TIMEOUT", cause);
    }
}
