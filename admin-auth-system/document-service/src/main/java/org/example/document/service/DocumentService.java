package org.example.document.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DocumentService {





    public String parseDocxToHtml(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            WordprocessingMLPackage wordMLPackage = Docx4J.load(inputStream);
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setImageDirPath(null);
            htmlSettings.setImageTargetUri(null);

            // Atomic counter for logging
            AtomicInteger imgCounter = new AtomicInteger(0);

            htmlSettings.setImageHandler(new org.docx4j.model.images.ConversionImageHandler() {
                @Override
                public String handleImage(org.docx4j.model.images.AbstractWordXmlPicture picture,
                                          org.docx4j.relationships.Relationship relationship,
                                          org.docx4j.openpackaging.parts.WordprocessingML.BinaryPart binaryPart) {
                    try {
                        int count = imgCounter.incrementAndGet();
                        System.out.println("Processing image #" + count + " for Base64 embedding...");
                        byte[] imageBytes = binaryPart.getBytes();
                        String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);

                        // Simple mime type detection based on magic numbers
                        String mimeType = "image/png"; // default
                        if (imageBytes.length > 3) {
                            if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
                                mimeType = "image/jpeg";
                            } else if (imageBytes[0] == (byte) 0x47 && imageBytes[1] == (byte) 0x49
                                    && imageBytes[2] == (byte) 0x46) {
                                mimeType = "image/gif";
                            } else if (imageBytes[0] == (byte) 0x42 && imageBytes[1] == (byte) 0x4D) {
                                mimeType = "image/bmp";
                            }
                        }

                        return "data:" + mimeType + ";base64," + base64;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            });

            htmlSettings.setWmlPackage(wordMLPackage);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Using FLAG_NONE (0) usually defaults to XSL.
            Docx4J.toHTML(htmlSettings, out, Docx4J.FLAG_NONE);

            String fullHtml = new String(out.toByteArray());

            // Extract only the body content using Jsoup to ensure TipTap receives a clean
            // fragment
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(fullHtml);

            int totalImgs = doc.select("img").size();
            System.out.println("Total <img> tags in generated HTML: " + totalImgs);

            // Post-process: Remove height attribute from images to preventing <img
            // height="21" ...> issues
            // BUT keep 'width' and 'style' to preserve the document's intended horizontal
            // scaling.
            // CSS 'height: auto' in frontend will ensure aspect ratio is maintained based
            // on the width.
            doc.select("img").removeAttr("height");
            // REMOVED: doc.select("div").unwrap();
            // Reason: docx4j often applies alignment (text-align: center) to wrapper divs.
            // Unwrapping destroys this layout.
            // TipTap should handle nested divs reasonably well for display.

            return doc.body().html();
        }
    }

    public byte[] convertHtmlToDocx(String htmlContent) throws Exception {
        // Ensure proper XHTML structure using Jsoup
        // docx4j-ImportXHTML requires strictly well-formed XML (XHTML)
        org.jsoup.nodes.Document soupDoc = org.jsoup.Jsoup.parse(htmlContent);
        soupDoc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        soupDoc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);

        // Convert to XHTML string
        String xhtml = soupDoc.html();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

        // Custom Image Handler for resolving Base64 images from HTML during SAVE
        // XHTMLImporter.setHyperlinkHandler(null); // Optional: Handle links if needed

        // Convert the XHTML to Word ML Package
        wordMLPackage.getMainDocumentPart().getContent().addAll(
                XHTMLImporter.convert(xhtml, null)); // null baseUri

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wordMLPackage.save(out);
        return out.toByteArray();
    }


    public byte[] generateDocxFromTemplate(MultipartFile templateFile , Map<String, Object> inputJson) throws Exception{


            ObjectMapper mapper = new ObjectMapper();

            // 2️⃣ Convert Input to Case-Insensitive Map
            Map<String, String> caseInsensitiveInput = new HashMap<>();
            inputJson.forEach((k, v) -> caseInsensitiveInput.put(k.toLowerCase(), String.valueOf(v)));

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

            // Support standard ${key}, {{key}} and simple {key} formats
            java.util.regex.Pattern pattern = java.util.regex.Pattern
                    .compile("(\\$\\{([^}]+)\\})|(\\{\\{([^}]+)\\}\\})|(\\{([^}]+)\\})");

            for (org.docx4j.openpackaging.parts.JaxbXmlPart<?> part : targetParts) {
                String xmlContent = part.getXML();
                java.util.regex.Matcher matcher = pattern.matcher(xmlContent);

                StringBuilder sb = new StringBuilder();
                boolean modified = false;

                while (matcher.find()) {
                    // Group 2: ${key}, Group 4: {{key}}, Group 6: {key}
                    String key;
                    if (matcher.group(2) != null) {
                        key = matcher.group(2);
                    } else if (matcher.group(4) != null) {
                        key = matcher.group(4);
                    } else {
                        key = matcher.group(6);
                    }

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
            return out.toByteArray();

    }


    }


