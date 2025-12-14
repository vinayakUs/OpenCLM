package com.example.notification_service.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    public String render(String template, Map<String, Object> variables) {
        if (template == null) {
            return "";
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            String value = String.valueOf(entry.getValue());
            result = result.replace(key, value);
        }
        return result;
    }
}
