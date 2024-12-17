package com.fpt.sep490.service;

import com.fpt.sep490.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductWarehouseSpecification {
    public Specification<ProductWarehouse> hasProductCodeOrProductNameOrBatchCodeOrImportDate(
            String productCode, String productName, Long categoryId, Long supplierId, Long warehouseId) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productCode != null && !productCode.isEmpty()) {
                Join<ProductWarehouse, Product> productJoin = root.join("product");
                predicates.add(criteriaBuilder.like(productJoin.get("productCode"), "%" + productCode + "%"));
            }

            if (productName != null && !productName.isEmpty()) {
                Join<ProductWarehouse, Product> productJoin = root.join("product");
                predicates.add(criteriaBuilder.like(productJoin.get("name"), "%" + productName + "%"));
            }

            if (warehouseId != null) {
                Join<ProductWarehouse, Warehouse> warehouseJoin = root.join("warehouse");
                predicates.add(criteriaBuilder.equal(warehouseJoin.get("id"), warehouseId));
            }

            if (categoryId != null) {
                Join<ProductWarehouse, Product> productJoin = root.join("product");
                Join<Product, Category> categoryJoin = productJoin.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
            }

            if (supplierId != null) {
                Join<ProductWarehouse, Product> productJoin = root.join("product");
                Join<Product, Supplier> supplierJoin = productJoin.join("supplier");
                predicates.add(criteriaBuilder.equal(supplierJoin.get("id"), supplierId));
            }
            Join<ProductWarehouse, Product> productJoin = root.join("product");
            predicates.add(criteriaBuilder.equal(productJoin.get("isDeleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
