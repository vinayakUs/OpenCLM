package com.example.bff.domain.contract.dto;

import java.util.Map;
import java.util.UUID;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewContractRequest {
    @NotNull(message = "Workflow ID is required")
    private UUID workflowId;

    @NotBlank(message = "Contract name is required")
    private String contractName;

    @NotNull(message = "Form data is required")
    private Map<String, String> formData;
}
