package org.example.document.listener;


import com.example.common.dto.contract.ContractEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document.service.DocumentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContractListener {

    private final DocumentService documentService;



    @KafkaListener(topics = "CONTRACT")
    public void listen(@Payload ContractEvent contract)
    {
        log.info("Received Contract Event: {}", contract.getPayload().toString());

//        documentService.generateDocxFromTemplate()



    }


}
