package com.example.contract.service;

import com.example.contract.comman.ContractEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ContractEventProducer {

    @Value("${app.kafka.topic.contract}")
    private String contractTopic;

    private final KafkaTemplate<String,ContractEvent> kafkaTemplate;


    private ProducerRecord<String,ContractEvent> createRecord(
            String key,
            ContractEvent contractEvent
    ){
        return new ProducerRecord<>(
                contractTopic,
                key,
                contractEvent
        );
    }

//    public String send(final ContractEvent event){

//        final ProducerRecord<String,ContractEvent> record = createRecord(String.valueOf(event.getId()),event);
//        CompletableFuture<SendResult<String, ContractEvent>> future = kafkaTemplate.send(record);
//
//        future.whenComplete((res,err)->{
//            if(err==null){
//                return "ok";
//            }else {}
//        })

//    }



}
