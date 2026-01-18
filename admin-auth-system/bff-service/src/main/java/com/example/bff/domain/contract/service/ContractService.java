package com.example.bff.domain.contract.service;

import com.example.bff.domain.contract.client.ContractClient;
import com.example.bff.domain.contract.dto.ContractResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractClient contractClient;

    public Mono<List<ContractResponse>> getContract(String q){

        return contractClient.getContracts(q);
    }

}
