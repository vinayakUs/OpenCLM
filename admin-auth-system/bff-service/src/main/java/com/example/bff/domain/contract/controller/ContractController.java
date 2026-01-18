package com.example.bff.domain.contract.controller;

import com.example.bff.domain.contract.client.ContractClient;
import com.example.bff.domain.contract.dto.ContractResponse;
import com.example.bff.domain.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/contract")
@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;


    @GetMapping("/")
    public Mono<List<ContractResponse>> getContract(
            @RequestParam(required = false) String query
    )
    {
        return contractService.getContract(query);
    }

}
