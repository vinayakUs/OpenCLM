package com.example.bff.shared;

public enum ApiErrorCode {

    /* =======================
     *  4xx — Client Errors
     * ======================= */

    INVALID_REQUEST("INVALID_REQUEST"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    MISSING_REQUIRED_FIELD("MISSING_REQUIRED_FIELD"),
    INVALID_FIELD_VALUE("INVALID_FIELD_VALUE"),
    MALFORMED_PAYLOAD("MALFORMED_PAYLOAD"),
    UNSUPPORTED_OPERATION("UNSUPPORTED_OPERATION"),

    /* =======================
     *  401 / 403 — Security
     * ======================= */

    UNAUTHORIZED("UNAUTHORIZED"),
    ACCESS_DENIED("ACCESS_DENIED"),
    TOKEN_EXPIRED("TOKEN_EXPIRED"),
    TOKEN_INVALID("TOKEN_INVALID"),

    /* =======================
     *  404 — Resource
     * ======================= */

    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    CONTRACT_NOT_FOUND("CONTRACT_NOT_FOUND"),

    /* =======================
     *  409 — Conflict
     * ======================= */

    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE"),
    CONTRACT_ALREADY_EXISTS("CONTRACT_ALREADY_EXISTS"),
    VERSION_CONFLICT("VERSION_CONFLICT"),

    /* =======================
     *  422 — Business Rules
     * ======================= */

    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION"),
    CONTRACT_STATE_INVALID("CONTRACT_STATE_INVALID"),
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED"),

    /* =======================
     *  5xx — Server / Infra
     * ======================= */

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
    DEPENDENCY_FAILURE("DEPENDENCY_FAILURE"),
    TIMEOUT("TIMEOUT"),
    DATABASE_ERROR("DATABASE_ERROR"),
    BAD_GATEWAY("BAD_GATEWAY"),

    /* =======================
     *  5xx — Contract Domain
     * ======================= */

    CONTRACT_INTERNAL_ERROR("CONTRACT_INTERNAL_ERROR"),
    CONTRACT_PROCESSING_FAILED("CONTRACT_PROCESSING_FAILED");

    private final String code;

    ApiErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
