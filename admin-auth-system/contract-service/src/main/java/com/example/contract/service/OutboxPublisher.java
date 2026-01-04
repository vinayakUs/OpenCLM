package com.example.contract.service;

import com.example.contract.comman.ContractEvent;
import com.example.contract.entity.OutboxEvent;
import com.example.contract.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    @Value("${app.kafka.topic.contract}")
    private String contractTopic;


    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String,ContractEvent> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publish(){

        List<OutboxEvent> eventList = outboxRepository.findTop50ByStatusOrderByCreatedAt("NEW");

        for(OutboxEvent event:eventList){

            ContractEvent contractEvent = ContractEvent
                    .builder()
                    .eventId(event.getEventId())
                    .eventType(event.getEventType())
                    .aggregateId(event.getAggregateId())
                    .occurredAt(event.getCreatedAt())
                    .payload(event.getPayload())
                    .build();

            final ProducerRecord<String,ContractEvent> record  = createRecord(
                    event.getAggregateId(),
                    contractEvent

            );
            CompletableFuture<SendResult<String,ContractEvent>> future= kafkaTemplate.send(record);
            future.whenComplete((res,err)->{
                System.out.println("result" + res+ err);
               if(err==null){
                   event.setStatus("SENT");
               }else {
                   event.setStatus("FAILED");
                   event.setRetryCount(event.getRetryCount()+1);
               }
                event.setLastAttemptAt(OffsetDateTime.now());
               outboxRepository.save(event);
            });

        }


    }

    public ProducerRecord<String,ContractEvent> createRecord(UUID key ,
                                                             ContractEvent contractEvent
                                                             ){
        return new ProducerRecord<>(
                contractTopic,
                String.valueOf(key) ,
                contractEvent
        ) ;
    }
}
