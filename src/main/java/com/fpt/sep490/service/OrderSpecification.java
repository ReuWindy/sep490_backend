package com.fpt.sep490.service;

import com.fpt.sep490.model.Order;
import com.fpt.sep490.model.Supplier;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("customer").get("id"), customerId);
        };
    }

    public static Specification<Order> hasOrderCode(String orderCode) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("orderCode"), orderCode);
        };
    }

    public static Specification<Order> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> hasNameOrHasStatus(String name, String status){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("name"), name));
            }
            if(status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("status"), "%" + status + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(predicates.toArray(new Predicate[0])));
        };
    }
}
