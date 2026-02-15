package com.example.bff.domain.contract.client;

import com.example.bff.domain.contract.dto.ContractResponse;
import com.example.bff.domain.contract.exception.ContractException;
import com.example.bff.domain.contract.exception.ContractInternalErrorException;
import com.example.bff.exception.comman.DownstreamInternalErrorException;
import com.example.bff.exception.comman.DownstreamTimeoutException;
import com.example.bff.exception.comman.DownstreamUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class ContractClient {

    private final WebClient webClient;

    public ContractClient(
            @Qualifier("default-web-client") WebClient.Builder builder) {
        this.webClient = builder.baseUrl("lb://contract-service")
                .filter(((request, next) -> {
                    log.info("request {}", request.url());
                    return next.exchange(request);
                }))
                .build();
    }

    public Mono<UUID> createContract(
            Map<String, Object> postBody) {

        return webClient.post()
                .uri(
                        uriBuilder -> uriBuilder.path("/contract").build())
                .bodyValue(postBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> {
                            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.error(new ContractException("Invalid workflow or contract data", null));
                            }
                            return Mono.error(new ContractException("Client error while creating contract", null));

                        })
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> {
                            if (clientResponse.statusCode() == HttpStatus.SERVICE_UNAVAILABLE ||
                                    clientResponse.statusCode() == HttpStatus.GATEWAY_TIMEOUT ||
                                    clientResponse.statusCode() == HttpStatus.BAD_GATEWAY) {
                                return Mono.error(new DownstreamUnavailableException("Contract Service", null));
                            }
                            return Mono.error(new DownstreamInternalErrorException("Contract Service", null));
                        })
                .bodyToMono(UUID.class)
                .onErrorMap(
                        throwable -> {

                            if (throwable instanceof ContractException) {
                                return throwable;
                            }

                            // service discovery has zero instances
                            if (throwable instanceof IllegalStateException &&
                                    throwable.getMessage() != null &&
                                    throwable.getMessage().contains("No instances available")) {
                                return new DownstreamUnavailableException("Contract Service", throwable);
                            }

                            if (throwable instanceof WebClientRequestException ex) {
                                Throwable cause = throwable.getCause();
                                if (cause instanceof UnknownHostException || cause instanceof ConnectException) {
                                    return new DownstreamUnavailableException("Contract Service", throwable);
                                }
                            }

                            if (throwable instanceof TimeoutException ex) {
                                return new DownstreamTimeoutException("Contract Service", throwable);
                            }
                            return new ContractInternalErrorException("Unexpected error: " + throwable.getMessage(),
                                    throwable);

                        });

    }

    public Mono<List<ContractResponse>> getContracts(String q) {

        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/contract");
                    if (q != null && !q.isBlank()) {
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
                                .map(ContractException::new))
                .bodyToFlux(ContractResponse.class)
                .collectList()
                .onErrorMap(WebClientRequestException.class,
                        ex -> new ContractException("Contract service unreachable", ex))
                .onErrorMap(WebClientResponseException.class,
                        ex -> new ContractException(
                                "Contract service error: " + ex.getStatusCode(), ex));
    }
}
