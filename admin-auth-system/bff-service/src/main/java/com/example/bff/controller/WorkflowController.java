package com.example.bff.controller;

import com.example.bff.dto.WorkflowRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    @Autowired
    @Qualifier("default-web-client")
    private WebClient.Builder webClientBuilder;

    @PostMapping("/save")
    public String postWorkflow(@RequestBody WorkflowRequest workflowRequest , @RequestPart MultipartFile file) {



        return entity;
    }

}
