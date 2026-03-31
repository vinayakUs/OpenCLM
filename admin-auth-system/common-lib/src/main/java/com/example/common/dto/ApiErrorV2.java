package com.example.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ApiErrorV2 {
    private String code; // Machine code: "USER_NOT_FOUND"
    private String message; // Human message: "User with ID 123 not found"
    private List<String> details; // Validation errors: ["Email invalid", "Age required"]
}
