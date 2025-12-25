package com.example.workflow.dto;

import lombok.Data;
import com.example.common.dto.VariableDataType;

@Data
public class VariableCreateRequest {
    private String variableName;
    private VariableDataType dataType;
    private String label;
    private Boolean required;
    private String defaultValue;
    private Integer sortValue;
}
