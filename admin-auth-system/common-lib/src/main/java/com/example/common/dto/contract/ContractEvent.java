package com.example.common.dto.contract;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ContractEvent {
    private UUID eventId;
    private String eventType;
    private UUID aggregateId;
    private OffsetDateTime occurredAt;
    private Map<String, Object> payload;

}
