package com.example.bff.domain.document.service;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class DocumentService {

    public String parseDocxToHtml(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            WordprocessingMLPackage wordMLPackage = Docx4J.load(inputStream);
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setImageDirPath(System.getProperty("java.io.tmpdir"));
            htmlSettings.setImageTargetUri(System.getProperty("java.io.tmpdir"));
            htmlSettings.setWmlPackage(wordMLPackage);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Docx4J.toHTML(htmlSettings, out, Docx4J.FLAG_EXPORT_PREFER_XSL);

            return new String(out.toByteArray());
        }
    }
}


