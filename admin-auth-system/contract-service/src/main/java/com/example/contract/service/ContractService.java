package com.example.contract.service;

import com.example.common.dto.contract.ContractStatus;
import com.example.contract.ContractResponse;
import com.example.contract.CreateContractRequest;
import com.example.contract.entity.Contract;
import com.example.contract.entity.OutboxEvent;
import com.example.contract.repository.ContractRepository;
import com.example.contract.repository.OutboxRepository;
import com.example.contract.repository.ProcessedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;
    private final ContractRepository contractRepository;


    @Transactional(rollbackOn = Exception.class)
    public UUID createContract(CreateContractRequest dto)  {
            Contract contract = new Contract(
                    dto.getName(),
                    dto.getWorkflowId()
            );

            Contract savedContract = contractRepository.save(contract);

            Map<String, Object> payload = Map.of(
                    "contractId", savedContract.getId(),
                    "contractName", savedContract.getName(),
                    "workflowId", savedContract.getWorkflowId(),
                    "createdBy", savedContract.getCreatedBy(),
                    "status", savedContract.getStatus(),
                    "formData", dto.getFormData()
            );

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(contract.getId())
                    .aggregateType("CONTRACT")
                    .eventType("CONTRACT_CREATED")
                    .eventId(UUID.randomUUID())
                    .payload(payload)
                    .status("NEW")
                    .build();

            outboxRepository.save(event);

            return contract.getId();

    }

    public List<ContractResponse> getContracts(String query) {
        List<Contract> contracts;
        if(query != null && !query.isBlank()){

            contracts  = contractRepository.findByNameContainingIgnoreCase(query);

        }else {
            contracts = contractRepository.findAll();

        }


       return contracts.stream().map(x->
             new ContractResponse(
                     x.getId(),
                     x.getName(),
                     x.getWorkflowId(),
                     x.getStatus(),
                     x.getCreatedBy(),
                     x.getCreatedAt()

             )
        ).toList();


    }
}
