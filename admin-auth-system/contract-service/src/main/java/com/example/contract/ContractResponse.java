package com.example.contract;

import com.example.common.dto.contract.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponse {
     UUID id;
     String name;
     UUID workflowId;
     ContractStatus status ;
     UUID createdBy;
     OffsetDateTime createdAt;

}
