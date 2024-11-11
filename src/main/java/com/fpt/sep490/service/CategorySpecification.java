package com.fpt.sep490.service;

import com.fpt.sep490.model.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {
    public static Specification<Category> hasNameAndStatus(String name, Boolean status) {
        return (root, query, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if(name != null && !name.isEmpty()) {
              predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
          }
          if (status != null) {
              predicates.add(criteriaBuilder.equal(root.get("active"), status));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}