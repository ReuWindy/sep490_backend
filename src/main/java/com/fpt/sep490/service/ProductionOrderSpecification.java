package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.model.ProductionOrder;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ProductionOrderSpecification {
    public static Specification<ProductionOrder> hasStatus(StatusEnum status) {
        return (root, query, criteriaBuilder) -> status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    }

    public static Specification<ProductionOrder> hasProductionDateAfter(Date startDate) {
        return (root, query, criteriaBuilder) -> startDate != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("productionDate"), startDate) : null;
    }

    public static Specification<ProductionOrder> hasProductionDateBefore(Date endDate) {
        return (root, query, criteriaBuilder) -> endDate != null ? criteriaBuilder.lessThanOrEqualTo(root.get("productionDate"), endDate) : null;
    }

    public static Specification<ProductionOrder> hasProductName(String productName) {
        return (root, query, criteriaBuilder) -> productName != null && !productName.isEmpty() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("productWarehouse").get("product").get("name")), "%" + productName.toLowerCase() + "%") : null;
    }
}
