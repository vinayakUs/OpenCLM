package com.example.bff.domain.document.client;

import com.example.bff.exception.DocumentProcessingException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class DocumentClient {

    private final WebClient webClient;

    public DocumentClient(@Qualifier("default-web-client") WebClient.Builder builder,
            @org.springframework.beans.factory.annotation.Value("${app.apis.document}") String documentUrl) {
        this.webClient = builder.baseUrl(documentUrl).build();
    }

    public String parseDocxToHtml(MultipartFile file) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            Map response = webClient.post()
                    .uri("service/document/convert/docxToHtml")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return (String) response.get("html");

        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to read file", e);
        } catch (WebClientResponseException e) {
            throw new DocumentProcessingException("Document Service Error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new DocumentProcessingException("Unexpected error during document conversion", e);
        }
    }

    public byte[] convertHtmlToDocx(String html) {
        try {
            return webClient.post()
                    .uri("service/document/convert/HtmlToDocx")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Collections.singletonMap("html", html))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new DocumentProcessingException("Document Service Error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new DocumentProcessingException("Unexpected error during HTML to DOCX conversion", e);
        }
    }
}
