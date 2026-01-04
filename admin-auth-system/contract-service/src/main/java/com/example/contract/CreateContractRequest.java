package com.example.contract;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateContractRequest {
    /**
     * Workflow from which the contract is created
     */
    @NotNull
    private UUID workflowId;

    /**
     * Human readable contract name
     * Example: "NDA – ABC Pvt Ltd"
     */
    @NotBlank
    private String name;

    /**
     * Data entered by user for workflow variables
     * Key = variable_name
     * Value = actual value
     */
    @NotEmpty
    private Map<String, Object> formData;
}
