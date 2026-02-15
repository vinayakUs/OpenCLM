package com.example.contract.controller;

import com.example.common.dto.PageResponse;
import com.example.common.dto.contract.ContractStatus;
import com.example.contract.ContractResponse;
import com.example.contract.CreateContractRequest;
import com.example.contract.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping()
    public ResponseEntity<UUID> createContract(@Valid @RequestBody CreateContractRequest dto) {
        return ResponseEntity.ok().body(contractService.createContract(dto));
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<ContractResponse>> getContract(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam() int page,
            @RequestParam() int pageSize) {
        return ResponseEntity.ok().body(contractService.getContracts(query, status, page, pageSize));
    }

}