package com.example.bff.domain.contract.client;

import com.example.bff.domain.contract.dto.ContractResponse;
import com.example.bff.domain.contract.exception.ContractException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ContractClient {

    private final WebClient webClient;

    public ContractClient(
            @Qualifier("default-web-client") WebClient.Builder builder,
            @Value("${app.apis.gateway}") String workflowUrl
    ) {
        this.webClient = builder.baseUrl(workflowUrl).build();
    }

    public Mono<List<ContractResponse>> getContracts(String q) {

        return webClient.get()
                .uri(uriBuilder ->
                        {
                            uriBuilder.path("/contract/");
                            if(q != null && !q.isBlank()){
                                uriBuilder.queryParam("query", q);
                            }
                                 return uriBuilder.build();
                        }

                )
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("Contract get error")
                                .map(ContractException::new)
                )
                .bodyToFlux(ContractResponse.class)
                .collectList()
                .onErrorMap(WebClientRequestException.class,
                        ex -> new ContractException("Contract service unreachable", ex)
                )
                .onErrorMap(WebClientResponseException.class,
                        ex -> new ContractException(
                                "Contract service error: " + ex.getStatusCode(), ex)
                );
    }
}
