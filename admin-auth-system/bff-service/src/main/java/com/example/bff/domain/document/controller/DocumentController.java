package com.example.bff.domain.document.controller;

import com.example.bff.domain.document.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String htmlContent = documentService.parseDocxToHtml(file);
            return ResponseEntity.ok(Collections.singletonMap("html", htmlContent));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Failed to parse document: " + e.getMessage()));
        }
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertDocument(@RequestBody Map<String, String> request) {
        try {
            String html = request.get("html");
            if (html == null) {
                return ResponseEntity.badRequest().build();
            }
            byte[] docxBytes = documentService.convertHtmlToDocx(html);

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"converted.docx\"")
                    .contentType(org.springframework.http.MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .body(docxBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
