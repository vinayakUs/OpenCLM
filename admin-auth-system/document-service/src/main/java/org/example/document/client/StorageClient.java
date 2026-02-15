package org.example.document.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@Slf4j
public class StorageClient {

    private WebClient webClient;

    public StorageClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://storage-service").build();
    }

    public void getDocument(UUID documentId)
    {
        this.webClient.get().uri("/documents/"+documentId).accept(MediaType.APPLICATION_JSON);
    }



}
