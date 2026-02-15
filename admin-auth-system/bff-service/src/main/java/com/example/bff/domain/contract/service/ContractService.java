package com.example.bff.domain.contract.service;

import com.example.bff.domain.contract.client.ContractClient;
import com.example.bff.domain.contract.dto.ContractResponse;
import com.example.bff.domain.contract.dto.NewContractRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {

    private final ContractClient contractClient;

    public Mono<List<ContractResponse>> getContract(String q) {

        return contractClient.getContracts(q);
    }

    public Mono<UUID> createContract(NewContractRequest request) {


        Map<String,Object> postBody = new HashMap<>();
        postBody.put("workflowId", request.getWorkflowId());
        postBody.put("name", request.getContractName());
        postBody.put("formData", request.getFormData());
        log.info("postBody: {}", postBody);


        return this.contractClient.createContract(
               postBody
        ).doOnNext(x -> log.info("uuid: {}", x));

     }

}
