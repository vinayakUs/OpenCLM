package org.example.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.example.document.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/service/document")
public class DocumentController {

    private final DocumentService documentService;


    @PostMapping("/convert/docxToHtml")
    public ResponseEntity<Map<String,String>> docxToHtml (@RequestParam("file") MultipartFile file)
    {
        try{
            String htmlContent = documentService.parseDocxToHtml(file);
            return ResponseEntity.ok(Collections.singletonMap("html", htmlContent));

        }catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error",
                            "Failed to parse document: " + e.getMessage()));
        }


    }

    @PostMapping("/convert/HtmlToDocx")
    public ResponseEntity<byte[]> htmlToDocx(@RequestBody Map<String, String> request) {
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


    @PostMapping(value = "/convert/generateFromTemplate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generate(
            @RequestPart("template") MultipartFile templateFile,
            @RequestPart("data") String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1️⃣ Parse JSON
            Map<String, Object> input = mapper.readValue(jsonData, Map.class);

            // 2️⃣ Convert Input to Case-Insensitive Map
            Map<String, String> caseInsensitiveInput = new HashMap<>();
            input.forEach((k, v) -> caseInsensitiveInput.put(k.toLowerCase(), String.valueOf(v)));

            // 3️⃣ Load DOCX
            WordprocessingMLPackage pkg = WordprocessingMLPackage.load(
                    templateFile.getInputStream());

            // ⭐ IMPORTANT FIX
            // Handle potentially malformed docs that crash VariablePrepare (e.g. "100.0"
            // integer error)
            try {
                VariablePrepare.prepare(pkg);
            } catch (Exception e) {
                System.err.println(
                        "Warning: VariablePrepare failed (likely malformed DOCX), continuing without run checks: "
                                + e.getMessage());
            }

            // 4️⃣ Scan & Replace in ALL Parts (Main, Header, Footer)

            // Collect all parts that might contain variables
            java.util.List<org.docx4j.openpackaging.parts.JaxbXmlPart<?>> targetParts = new java.util.ArrayList<>();

            // Main Document
            targetParts.add(pkg.getMainDocumentPart());

            // Headers & Footers
            pkg.getParts().getParts().values().forEach(part -> {
                if (part instanceof org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart ||
                        part instanceof org.docx4j.openpackaging.parts.WordprocessingML.FooterPart) {
                    targetParts.add((org.docx4j.openpackaging.parts.JaxbXmlPart<?>) part);
                }
            });

            // Support standard ${key} and simple {key} formats
            java.util.regex.Pattern pattern = java.util.regex.Pattern
                    .compile("(\\$\\{([^}]+)\\})|(\\{([^}]+)\\})");

            for (org.docx4j.openpackaging.parts.JaxbXmlPart<?> part : targetParts) {
                String xmlContent = part.getXML();
                java.util.regex.Matcher matcher = pattern.matcher(xmlContent);

                StringBuilder sb = new StringBuilder();
                boolean modified = false;

                while (matcher.find()) {
                    // Group 2 is content of ${...}, Group 4 is content of {...}
                    String key = matcher.group(2) != null ? matcher.group(2) : matcher.group(4);

                    String inputVal = caseInsensitiveInput.get(key.toLowerCase());

                    if (inputVal != null) {
                        // XML Escape the value to prevent invalid XML
                        String escapedVal = inputVal.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;")
                                .replace("'", "&apos;");

                        matcher.appendReplacement(sb,
                                java.util.regex.Matcher.quoteReplacement(escapedVal));
                        modified = true;
                    }
                }
                matcher.appendTail(sb);

                if (modified) {
                    try {
                        // Unmarshal the modified XML string back to JAXB object
                        Object unmarshalled = org.docx4j.XmlUtils
                                .unmarshalString(sb.toString());
                        ((org.docx4j.openpackaging.parts.JaxbXmlPart) part)
                                .setJaxbElement(unmarshalled);
                    } catch (Exception e) {
                        System.err.println(
                                "Failed to apply changes to part: " + e.getMessage());
                        // Continue to next part, don't fail entire request
                    }
                }
            }
            // 5️⃣ Save output
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pkg.save(out);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=contract.docx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());

        } catch (

                Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage().getBytes());
        }
    }



}
