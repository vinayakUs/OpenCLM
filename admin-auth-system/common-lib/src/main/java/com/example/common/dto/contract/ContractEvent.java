package com.example.common.dto.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEvent {
    private UUID eventId;
    private String eventType;
    private UUID aggregateId;
    private OffsetDateTime occurredAt;
    private Map<String, Object> payload;

}
