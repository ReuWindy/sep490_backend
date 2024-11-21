package com.fpt.sep490.service;

import com.fpt.sep490.model.FinishedProduct;
import org.springframework.data.jpa.domain.Specification;

public class FinishedProductSpecification {
    public static Specification<FinishedProduct> hasIsActive(boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<FinishedProduct> belongsToProductName(String productName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%");
    }
}
