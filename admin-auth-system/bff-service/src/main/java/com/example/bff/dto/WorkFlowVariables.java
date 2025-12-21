package com.example.bff.dto;

import lombok.Data;

@Data
public class WorkFlowVariables {
    private String variableName;
    private VariableDataType dataType;
    private String label;
    private Boolean required;
    private String defaultValue;
    private Integer sortValue;
}