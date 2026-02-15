package com.example.contract.service;

import com.example.common.dto.PageResponse;
import com.example.common.dto.contract.ContractStatus;
import com.example.contract.ContractResponse;
import com.example.contract.CreateContractRequest;
import com.example.contract.entity.Contract;
import com.example.contract.entity.Contract_;
import com.example.contract.entity.OutboxEvent;
import com.example.contract.repository.ContractRepository;
import com.example.contract.repository.OutboxRepository;
import com.example.contract.specifications.ContractSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

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

    private ContractResponse mapContractToContractResponse(Contract contract){

        ContractResponse response = new ContractResponse();
        response.setId(contract.getId());
        response.setName(contract.getName());
        response.setWorkflowId(contract.getWorkflowId());
        response.setCreatedBy(contract.getCreatedBy());
        response.setCreatedAt(contract.getCreatedAt());
        response.setStatus(contract.getStatus());

        return response;
    }



    public PageResponse<ContractResponse> getContracts(String query , ContractStatus status , int page , int pageSize) {

        Specification<Contract> specification = Specification.where(ContractSpecification.filterByName(query))
                .and(ContractSpecification.filterByStatus(status));
        Pageable pageable = PageRequest.of(
                page,
                pageSize,
                JpaSort.of(Contract_.createdAt).descending()
        );

        Page<Contract> contracts = contractRepository.findAll(specification, pageable);

        Page<ContractResponse> mappedPage = contracts.map(this::mapContractToContractResponse);

        PageResponse<ContractResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(mappedPage.getContent());
        pageResponse.setPage(mappedPage.getNumber());
        pageResponse.setSize(mappedPage.getSize());
        pageResponse.setTotalPages(mappedPage.getTotalPages());
        pageResponse.setTotalElements(mappedPage.getTotalElements());
        pageResponse.setLast(mappedPage.isLast());
        return pageResponse;
    }
}
