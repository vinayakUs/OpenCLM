package com.example.workflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VariableResponse {
    private UUID id;
    private String variableName;   // e.g. party_name
    private VariableDataType dataType;       // STRING, DATE, Int
    private String label;          // Friendly UI label
    private Boolean required = false;
    private String defaultValue;
    private Integer sortOrder = 0;
    private LocalDateTime createdAt ;

}
