package com.example.bff.shared;


import io.opentelemetry.api.trace.Span;

public final class Utils {

    private Utils() {}

    public static String getTraceId() {
        return Span.current().getSpanContext().getTraceId();
    }
}