package com.example.bff.exception.comman;

public class DownstreamUnavailableException extends DownstreamException{
    public DownstreamUnavailableException(String service, Throwable cause) {
        super(service + " is unreachable", "SERVICE_UNAVAILABLE", cause);
    }
}
