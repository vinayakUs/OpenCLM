package com.example.workflow.specification;

import com.example.workflow.entity.WorkflowTemplate;
import com.example.workflow.entity.WorkflowTemplate_;
import org.springframework.data.jpa.domain.Specification;

public class WorkflowSpecification {

    public static Specification<WorkflowTemplate> filterName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(
                    root.get(WorkflowTemplate_.NAME)), "%" + name.toLowerCase() + "%");
        };

    }

}
