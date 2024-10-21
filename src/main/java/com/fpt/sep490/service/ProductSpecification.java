package com.fpt.sep490.service;

import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductSpecification {
    public Specification<Product> hasProductCodeOrProductNameOrBatchCodeOrImportDate(String productCode, String productName, String batchCode, Date importDate, String priceOrder, String sortDirection) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(productCode != null && !productCode.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("productCode"), productCode));
            }
            if(productName != null && !productName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + productName + "%"));
            }
            if(batchCode != null && !batchCode.isEmpty()) {
                Join<Product, BatchProduct> batchProductJoin = root.join("batchProducts");
                Join<BatchProduct, Batch> batchJoin = batchProductJoin.join("batch");
                predicates.add(criteriaBuilder.like(batchJoin.get("batchCode"), "%" + batchCode + "%"));
            }

            if (importDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createAt"), importDate));
            }

            List<Order> orders = getSortByField(root, criteriaBuilder, priceOrder, sortDirection);
            query.orderBy(orders);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Product> hasNameOrCategoryNameOrSupplierName(String name, String categoryName, String supplierName){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(name != null && !name.isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            if(categoryName != null && !categoryName.isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("category").get("name"), categoryName));
            }
            if(supplierName != null && !supplierName.isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("supplier").get("name"),supplierName));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<Order> getSortByField(Root<Product> root, CriteriaBuilder criteriaBuilder, String priceOrder, String sortDirection) {
        List<Order> orders = new ArrayList<>();

        if(priceOrder != null && !priceOrder.equalsIgnoreCase("asc")) {
            orders.add(criteriaBuilder.asc(root.get("price")));
        }
        if(priceOrder != null && !priceOrder.equalsIgnoreCase("desc")) {
            orders.add(criteriaBuilder.desc(root.get("price")));
        }

        if(sortDirection != null && !sortDirection.equalsIgnoreCase("asc")) {
            orders.add(criteriaBuilder.asc(root.get("createAt")));
        }
        if(sortDirection != null && !sortDirection.equalsIgnoreCase("desc")) {
            orders.add(criteriaBuilder.desc(root.get("createAt")));
        }
        if (orders.isEmpty()) {
            if(sortDirection != null && sortDirection.equalsIgnoreCase("asc")) {
                orders.add(criteriaBuilder.asc(root.get("createAt")));
            } else {
                orders.add(criteriaBuilder.desc(root.get("createAt")));
            }
        }
        return orders;
    }
}