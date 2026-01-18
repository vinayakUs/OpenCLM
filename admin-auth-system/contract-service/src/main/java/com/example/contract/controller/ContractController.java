package com.example.contract.controller;

import com.example.contract.ContractResponse;
import com.example.contract.CreateContractRequest;
import com.example.contract.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/")
    public ResponseEntity<?> createContract(@Valid @RequestBody CreateContractRequest dto){
            return ResponseEntity.ok().body(contractService.createContract(dto));
    }

    @GetMapping("/")
    public ResponseEntity<List<ContractResponse>> getContract(@RequestParam(required = false) String query){
        return ResponseEntity.ok().body(contractService.getContracts(query));
    }



}