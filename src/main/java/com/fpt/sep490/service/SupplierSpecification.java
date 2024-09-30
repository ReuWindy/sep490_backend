package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
public class SupplierSpecification  {
    public static Specification<Supplier> hasEmailOrNameOrPhoneNumber(String name, String phoneNumber, String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

