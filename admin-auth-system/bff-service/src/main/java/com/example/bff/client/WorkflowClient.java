package com.example.bff.client;

import com.example.bff.dto.WorkflowCreateRequest;
import com.example.bff.dto.WorkflowClientRequest;
import com.example.bff.dto.WorkflowResponse;
import com.example.bff.exception.WorkflowCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
                            clientResponse -> clientResponse.bodyToMono(String.class).map(body->new WorkflowCreationException("Workflow Failed : " + body,null))
                            )
                    .bodyToMono(WorkflowResponse.class)
                    .block();


        }catch (WebClientResponseException ex){
            throw new WorkflowCreationException("Workflow Service Error : " +ex.getLocalizedMessage(),ex);
        }catch (Exception ex){
            throw new WorkflowCreationException("Unexpected file upload error",ex);
        }


    }

}
