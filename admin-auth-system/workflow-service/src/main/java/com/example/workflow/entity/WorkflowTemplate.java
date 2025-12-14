package com.example.workflow.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_template")
@Data
public class WorkflowTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name ="name", nullable = false )
    public String name;

    @Column(name ="description" )
    public String description;

    @Column(name = "template_file_id" ,nullable = false )
    public UUID template_file_id;

    @Column(name = "current_status" )
    public String current_status = "DRAFT"; //DRAFT or LIVE

    @Column(name = "version" )
    public Integer version=1;

    @Column(name = "created_by" ,nullable = false )
    public UUID created_by;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at = LocalDateTime.now();

}

