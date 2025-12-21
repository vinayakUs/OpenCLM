package com.example.bff.client;

import com.example.bff.dto.FileUploadResponse;
import com.example.bff.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class StorageClient {

    private final WebClient webClient;

    StorageClient(@Qualifier("default-web-client") WebClient.Builder builder,
            @org.springframework.beans.factory.annotation.Value("${app.apis.storage}") String storageUrl) {
        this.webClient = builder.baseUrl(storageUrl).build();
    }

    public FileUploadResponse storeFileS3(MultipartFile file) {


                try {
                  return   webClient.post()
                            .uri("/storage/files/upload")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData("file", file.getResource()))
                            .retrieve()
                            .onStatus(status -> status.isError(), clientResponse ->
                                    clientResponse.bodyToMono(String.class).map(body -> new FileUploadException("File Upload Failed : " + body, null))
                            )
                            .bodyToMono(FileUploadResponse.class)
                          .block();
                }catch (WebClientResponseException ex){
                    throw new FileUploadException("Storage service error: " + ex.getStatusCode(), ex);
                }catch (Exception ex){
                    throw new FileUploadException("Unexpected file upload error", ex);
                }
    }

}
