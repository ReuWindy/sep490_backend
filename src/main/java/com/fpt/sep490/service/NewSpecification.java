package com.fpt.sep490.service;

import com.fpt.sep490.model.News;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NewSpecification {
    public static Specification<News> hasNameOrTypeOrCreatedBy(String name, String type, String username){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (username != null) {
                predicates.add(criteriaBuilder.equal(root.get("createBy").get("fullName"), username));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
