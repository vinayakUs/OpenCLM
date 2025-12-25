package com.example.bff.domain.document.client;

import com.example.bff.domain.document.dto.FileUploadResponse;
import com.example.bff.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class StorageClient {

    private final WebClient webClient;

    StorageClient(@Qualifier("default-web-client") WebClient.Builder builder,
            @org.springframework.beans.factory.annotation.Value("${app.apis.storage}") String storageUrl) {
        this.webClient = builder.baseUrl(storageUrl).build();
    }

    public FileUploadResponse storeFileS3(MultipartFile file) {


                try {
                    MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
                    multipartBodyBuilder.part(
                                    "file",
                                    file.getResource()
                            ).filename(file.getOriginalFilename())
                            .contentType(MediaType.parseMediaType(file.getContentType()))
                    ;


                    return   webClient.post()
                            .uri("/storage/files/upload")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(
                                    BodyInserters.fromMultipartData(multipartBodyBuilder.build())
                            )
                            .retrieve()
                            .onStatus(status -> status.isError(),
                                    clientResponse ->
                                            clientResponse.bodyToMono(String.class).flatMap(
                                                    body -> Mono.error(new FileUploadException("File Upload Failed : " + body, null)
                                                    )
                                            )
                            )
                            .bodyToMono(FileUploadResponse.class)
                          .block();
                } catch (WebClientRequestException ex) {
                    throw new FileUploadException("Storage service unreachable: ", ex);
                } catch (WebClientResponseException ex) {
                    throw new FileUploadException("Storage service error: " + ex.getStatusCode(), ex);
                }catch (Exception ex){
                    throw new FileUploadException("Unexpected file upload error", ex);
                }
    }

}


