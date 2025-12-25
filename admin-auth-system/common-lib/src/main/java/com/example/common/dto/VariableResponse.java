package com.example.common.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VariableResponse {
    private UUID id;
    private String variableName;
    private VariableDataType dataType; // Uses enum in same package
    private String label;
    private Boolean required = false;
    private String defaultValue;
    private Integer sortOrder = 0;
    private LocalDateTime createdAt;
}
