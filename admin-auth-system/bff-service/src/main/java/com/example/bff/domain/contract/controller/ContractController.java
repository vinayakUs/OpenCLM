package com.example.bff.domain.contract.controller;

import com.example.bff.domain.contract.dto.ContractResponse;
import com.example.bff.domain.contract.dto.NewContractRequest;
import com.example.bff.domain.contract.service.ContractService;
import com.example.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/contract")
@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/")
    public Mono<List<ContractResponse>> getContract(
            @RequestParam(required = false) String query) {
        return contractService.getContract(query);
    }

    @PostMapping("")
    public Mono<ResponseEntity<ApiResponse<UUID>>> createContract(@Validated @RequestBody NewContractRequest request) {
        return contractService.createContract(request).map(
                x -> ResponseEntity.ok().body(ApiResponse.success(x)));
    }

}
