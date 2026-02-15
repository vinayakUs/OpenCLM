package com.example.bff.domain.workflow.client;

import com.example.bff.domain.contract.exception.ContractInternalErrorException;
import com.example.bff.domain.workflow.dto.WorkflowClientRequest;
import com.example.bff.domain.workflow.exception.WorkflowCreationException;
import com.example.bff.domain.workflow.exception.WorkflowRetrievalException;
import com.example.bff.exception.comman.DownstreamInternalErrorException;
import com.example.bff.exception.comman.DownstreamTimeoutException;
import com.example.bff.exception.comman.DownstreamUnavailableException;
import com.example.bff.exception.comman.WorkflowNotFoundException;
import com.example.common.dto.ApiResponse;
import com.example.common.dto.PageResponse;
import com.example.common.dto.WorkflowResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
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
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
public class WorkflowClient {

    private final WebClient webClient;

    public WorkflowClient(@Qualifier("default-web-client") WebClient.Builder builder) {
         this.webClient = builder.baseUrl("lb://workflow-service").build();
    }

    public UUID postWorkflow(WorkflowClientRequest workflowClientRequest) {

        try {

            return webClient.post()
                    .uri("/workflow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(workflowClientRequest)

                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(body -> new WorkflowCreationException("Workflow Failed : " + body, null)))
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<UUID>>() {
                    })
                    .map(x->x.data)
                    .block();

        } catch (WebClientResponseException ex) {
            throw new WorkflowCreationException("Workflow Service Error : " + ex.getLocalizedMessage(), ex);
        } catch (Exception ex) {
            throw new WorkflowCreationException("Unexpected file upload error", ex);
        }

    }

public Mono<UUID> postworkflowAsync(WorkflowClientRequest workflowClientRequest) {

    return webClient.post()
            .uri("/workflow")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(workflowClientRequest)
            .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {
                            if(clientResponse.statusCode() == HttpStatus.NOT_FOUND){

                                return  clientResponse.createException().flatMap(
                                        x->  Mono.error(
                                                new WorkflowCreationException(
                                                        "Workflow endpoint not found",x
                                                )
                                        )
                                ) ;

                            }

                            return clientResponse.createException().flatMap(
                                    x->
                                            Mono.error(
                                                    new WorkflowCreationException(
                                                            "Invalid workflow request",x
                                                    )
                                            )
                            ) ;


                        }
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> {
                            if (clientResponse.statusCode() == HttpStatus.SERVICE_UNAVAILABLE ||
                                    clientResponse.statusCode() == HttpStatus.GATEWAY_TIMEOUT ||
                                    clientResponse.statusCode() == HttpStatus.BAD_GATEWAY
                            ) {
                                clientResponse.createException().flatMap(
                                        x->
                                                Mono.error(new DownstreamUnavailableException("Workflow Service", x))
                                ) ;

                            }

                            return  clientResponse.createException().flatMap(
                                    x->
                                            Mono.error(new DownstreamInternalErrorException("Workflow Service", x))
                            ) ;

                        }
                )
                .bodyToMono(UUID.class)
                .onErrorMap(
                        x->{
                            if(x instanceof WebClientRequestException ){
                                Throwable cause = x.getCause();
                                if(cause instanceof ConnectException||
                                    cause instanceof UnknownHostException){
                                    return new DownstreamUnavailableException("Contract Service", x) ;
                                }
                                if (cause instanceof TimeoutException ) {
                                    return new DownstreamTimeoutException("Workflow Service", x);
                                }

                            }  return   x;
                        }
                )


                ;

}


    public Mono<PageResponse<WorkflowResponse>> getAllWorkflow(String search, int page, int size,String sortBy, String direction) {


        return webClient.get().uri(
                uriBuilder -> {
                    uriBuilder.path("/workflow");
                    if (search != null && !search.isEmpty()) {
                        uriBuilder.queryParam("search", search);
                    }
                    uriBuilder.queryParam("page", page);
                    uriBuilder.queryParam("size", size);

                    uriBuilder.queryParam("sortBy", sortBy);
                    uriBuilder.queryParam("direction", direction);

                    return uriBuilder.build();
                }
        ).exchangeToMono(
                clientResponse -> {

                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResponse<WorkflowResponse>>>() {
                                })
                                .flatMap(
                                        x -> Mono.just(x.getData())
                                );
                    }

                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new WorkflowNotFoundException(search, null));
                    }

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(
                                    x -> Mono.error(new WorkflowRetrievalException("Workflow Retrieval Error : " + clientResponse.statusCode()
                                            + " " + x, null))
                            );


                }
        ).onErrorMap(
                throwable -> {

                    if (throwable instanceof WorkflowRetrievalException) {
                        return throwable;
                    }

                    if (throwable instanceof WebClientRequestException) {
                        Throwable cause = throwable.getCause();

                        if (cause instanceof ConnectException || cause instanceof UnknownHostException) {
                            return new DownstreamUnavailableException("Workflow Service", throwable);
                        }

                    }

                    if (throwable instanceof java.util.concurrent.TimeoutException) {
                        return new DownstreamTimeoutException("Workflow Service", throwable);
                    }

                    return new DownstreamInternalErrorException("Unexpected error: " + throwable.getMessage(), throwable);


                }
        );

//        return webClient.get().uri(
//                uriBuilder -> {
//                    uriBuilder.path("/workflow");
//                    if (search != null && !search.isEmpty()) {
//                        uriBuilder.queryParam("search", search);
//                    }
//                    uriBuilder.queryParam("page", page);
//                    uriBuilder.queryParam("size", size);
//                    return uriBuilder.build();
//                })
//                .retrieve()
//                .onStatus(
//                        HttpStatusCode::isError,
//                        clientResponse -> clientResponse.bodyToMono(String.class)
//                                .defaultIfEmpty("Client error")
//                                .flatMap(x -> Mono.error(
//                                        new WorkflowRetrivalException("Workflow Retrieval Failed : " + x, null))))
//                .bodyToMono(new ParameterizedTypeReference<ApiResponse<PageResponse<WorkflowResponse>>>() {
//                })
//                .map(ApiResponse::getData)
//                    .onErrorMap(WebClientResponseException.class,
//                        ex -> new WorkflowRetrivalException("Workflow service unreachable : " + ex.getStatusCode(),
//                                ex));

    }

    public Mono<WorkflowResponse> getWorkflowById(UUID id) {

        return webClient.get()
                .uri("/workflow/{id}", id)
                .exchangeToMono(
                        response -> {


                            if (response.statusCode().is2xxSuccessful()) {
                                return response.bodyToMono(new ParameterizedTypeReference<ApiResponse<WorkflowResponse>>() {
                                        })
                                        .flatMap(api -> {
                                            if (api == null || api.getData() == null) {
                                                // Type hint here ensures this branch is Mono<WorkflowResponse>
                                                return Mono.error(new WorkflowNotFoundException(id.toString(), null));
                                            }
                                            return Mono.just(api.getData());
                                        });
                            }

                            if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                                return Mono.error(new WorkflowNotFoundException(id.toString(), null));
                            }

                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("Client error")
                                    .flatMap(body ->
                                            Mono.error(new WorkflowRetrievalException("Workflow Service Error: " + response.statusCode() + " - " + body, null))
                                    );


                        }
                ).onErrorMap(throwable -> {

                    if (throwable instanceof WorkflowRetrievalException) {
                        return throwable; // <- very important
                    }

                    if (throwable instanceof WebClientRequestException requestEx) {
                        Throwable cause = requestEx.getCause();

                        if (cause instanceof UnknownHostException || cause instanceof ConnectException) {
                            return new DownstreamUnavailableException("Workflow Service", throwable);
                        }
                    }

                    if (throwable instanceof java.util.concurrent.TimeoutException) {
                        return new DownstreamTimeoutException("Workflow Service", throwable);
                    }
                    return new WorkflowRetrievalException("Unexpected error: " + throwable.getMessage(), throwable);
                });


    }
}
