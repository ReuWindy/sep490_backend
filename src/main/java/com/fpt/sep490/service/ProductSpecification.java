package com.fpt.sep490.service;

import com.fpt.sep490.model.*;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductSpecification {
    public Specification<Product> hasProductCodeOrProductNameOrBatchCodeOrImportDate(
            String productCode, String productName, Long categoryId, Long supplierId, Long warehouseId,
            String batchCode, Date importDate, String priceOrder, String sortDirection) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productCode != null && !productCode.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("productCode"), "%" + productCode.trim() + "%"));
            }

            if (productName != null && !productName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + productName.trim() + "%"));
            }

            if (categoryId != null) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
            }

            if (supplierId != null) {
                Join<Product, Supplier> supplierJoin = root.join("supplier");
                predicates.add(criteriaBuilder.equal(supplierJoin.get("id"), supplierId));
            }

            if (batchCode != null && !batchCode.isEmpty()) {
                Join<Product, BatchProduct> batchProductJoin = root.join("batchProducts");
                Join<BatchProduct, Batch> batchJoin = batchProductJoin.join("batch");
                predicates.add(criteriaBuilder.like(batchJoin.get("batchCode"), "%" + batchCode + "%"));
            }

            if (warehouseId != null) {
                Join<Product, ProductWarehouse> productWarehouseJoin = root.join("productWarehouses");
                Join<ProductWarehouse, Warehouse> warehouseJoin = productWarehouseJoin.join("warehouse");
                predicates.add(criteriaBuilder.equal(warehouseJoin.get("id"), warehouseId));
            }

            if (importDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createAt"), importDate));
            }

            if (query != null) {
                query.distinct(true);
                List<Order> orders = getSortByField(root, criteriaBuilder, priceOrder, sortDirection);
                query.orderBy(orders);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Product> hasNameOrProductCodeOrCategoryNameOrSupplierName(String name, String productCode, String categoryName, String supplierName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (productCode != null && !productCode.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("productCode"), "%" + productCode.trim() + "%"));
            }
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("category").get("name"), "%" + categoryName.trim() + "%"));
            }
            if (supplierName != null && !supplierName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("supplier").get("name"), "%" + supplierName.trim() + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.greaterThan(root.get("price"), 0));
            Join<Product, ProductWarehouse> productWarehouseJoin = root.join("productWarehouses", JoinType.LEFT);
            Predicate warehouseNamePredicate = criteriaBuilder.equal(productWarehouseJoin.get("warehouse").get("name"), "Kho Bán Hàng");
            if (    (name == null || name.isEmpty()) &&
                    (productCode == null || productCode.isEmpty()) &&
                    (categoryName == null || categoryName.isEmpty()) &&
                    (supplierName == null || supplierName.isEmpty())) {
                predicates.add(warehouseNamePredicate);
            } else {
                predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(productWarehouseJoin), warehouseNamePredicate));
            }
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Product> hasNameOrProductCodeOrCategoryNameOrSupplierName2(String name, String productCode, String categoryName, String supplierName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (productCode != null && !productCode.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("productCode"), "%" + productCode.trim() + "%"));
            }
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("category").get("name"), "%" + categoryName.trim() + "%"));
            }
            if (supplierName != null && !supplierName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("supplier").get("name"), "%" + supplierName.trim() + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Product> hasNameOrProductCodeOrCategoryNameOrSupplierNameAndNotNull(String name, String productCode, String categoryName, String supplierName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (productCode != null && !productCode.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("productCode"), "%" + productCode.trim() + "%"));
            }
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("category").get("name"), "%" + categoryName.trim() + "%"));
            }
            if (supplierName != null && !supplierName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("supplier").get("name"), "%" + supplierName.trim() + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.greaterThan(root.get("price"), 0));
            Join<Product, ProductWarehouse> productWarehouseJoin = root.join("productWarehouses", JoinType.LEFT);
            Predicate warehouseNamePredicate = criteriaBuilder.equal(productWarehouseJoin.get("warehouse").get("name"), "Kho Bán Hàng");
            if (    (name == null || name.isEmpty()) &&
                    (productCode == null || productCode.isEmpty()) &&
                    (categoryName == null || categoryName.isEmpty()) &&
                    (supplierName == null || supplierName.isEmpty())) {
                predicates.add(warehouseNamePredicate);
            } else {
                predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(productWarehouseJoin), warehouseNamePredicate));
            }
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<Order> getSortByField(Root<Product> root, CriteriaBuilder criteriaBuilder, String priceOrder, String sortDirection) {
        List<Order> orders = new ArrayList<>();

        if (priceOrder != null && !priceOrder.equalsIgnoreCase("asc")) {
            orders.add(criteriaBuilder.asc(root.get("importPrice")));
        }
        if (priceOrder != null && !priceOrder.equalsIgnoreCase("desc")) {
            orders.add(criteriaBuilder.desc(root.get("importPrice")));
        }

        if (sortDirection != null && !sortDirection.equalsIgnoreCase("asc")) {
            orders.add(criteriaBuilder.asc(root.get("createAt")));
        }
        if (sortDirection != null && !sortDirection.equalsIgnoreCase("desc")) {
            orders.add(criteriaBuilder.desc(root.get("createAt")));
        }
        if (orders.isEmpty()) {
            orders.add(criteriaBuilder.desc(root.get("createAt")));
        }
        return orders;
    }
}