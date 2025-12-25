package com.example.bff.domain.workflow.client;

import com.example.bff.domain.workflow.dto.WorkflowCreateRequest;
import com.example.bff.domain.workflow.dto.WorkflowClientRequest;
import com.example.common.dto.WorkflowResponse;
import com.example.common.dto.PageResponse;
import com.example.common.dto.ApiResponse;
import com.example.bff.exception.WorkflowCreationException;
import com.example.bff.exception.WorkflowRetrivalException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class WorkflowClient {

    private final WebClient webClient;

    public WorkflowClient(@Qualifier("default-web-client") WebClient.Builder builder,
            @org.springframework.beans.factory.annotation.Value("${app.apis.workflow}") String workflowUrl) {
        this.webClient = builder.baseUrl(workflowUrl).build();
    }

    public WorkflowResponse postWorkflow(WorkflowClientRequest workflowClientRequest) {

        try {

            return webClient.post()
                    .uri("workflow/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(workflowClientRequest)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(body -> new WorkflowCreationException("Workflow Failed : " + body, null)))
                    .bodyToMono(WorkflowResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {
            throw new WorkflowCreationException("Workflow Service Error : " + ex.getLocalizedMessage(), ex);
        } catch (Exception ex) {
            throw new WorkflowCreationException("Unexpected file upload error", ex);
        }

    }

    public PageResponse<WorkflowResponse> getAllWorkflow(int page, int size) {
        try {
            ApiResponse<PageResponse<WorkflowResponse>> response = webClient.get()
                    .uri(x -> x.path("workflow/").queryParam("page", page).queryParam("size", size).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(
                            httpStatusCode -> httpStatusCode.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).map(
                                    body -> new WorkflowRetrivalException("Workflow Retrieval Failed : " + body, null)))
                    .bodyToMono(
                            new org.springframework.core.ParameterizedTypeReference<ApiResponse<PageResponse<WorkflowResponse>>>() {
                            })
                    .block();

            return response.getData();

        } catch (WebClientRequestException ex) {
            throw new WorkflowRetrivalException("Workflow Service Error : " + ex.getLocalizedMessage(), ex);
        } catch (Exception ex) {
            throw new WorkflowRetrivalException("Unexpected workflow get error: " + ex.getMessage(), ex);
        }

    }
}


