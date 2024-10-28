package com.fpt.sep490.service;

import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.ProductWarehouse;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

//public class ProductWareHouseSpecification {
//    public static Specification<ProductWarehouse> hasUnitOrHasWeightPerUnitOrCategoryOrSupplierOrWarehouse(String unit, double weightPerUnit, int categoryId, int supplierId, int warehouseId, String sortDirection, String priceOrder) {
//        return (root, query, builder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            if (unit != null && !unit.isEmpty()) {
//                predicates.add(builder.equal(root.get("unit"), unit));
//            }
//            if (weightPerUnit > 0) {
//                predicates.add(builder.equal(root.get("weightPerUnit"), weightPerUnit));
//            }
//            if (categoryId > 0) {
//                predicates.add(builder.equal(root.get("category_id").get("id"), categoryId));
//            }
//            if (supplierId > 0) {
//                predicates.add(builder.equal(root.get("supplier").get("id"), supplierId));
//            }
//            if (warehouseId > 0) {
//                predicates.add(builder.equal(root.get("warehouse").get("id"), warehouseId));
//            }
//            List<Order> orders = getSortByField(root, builder, sortDirection, priceOrder);
//            assert query != null;
//            query.orderBy(orders.toArray(new Order[0]));
//            return builder.and(predicates.toArray(new Predicate[0]));
//        };
//    }
//
//    private static List<Order> getSortByField(Root<ProductWarehouse> root, CriteriaBuilder criteriaBuilder, String sortDirection, String priceOrder) {
//        List<Order> orders = new ArrayList<>();
//        if(priceOrder != null && priceOrder.equalsIgnoreCase("asc")) {
//            orders.add(criteriaBuilder.asc(root.get("price")));
//        }else if(priceOrder != null && priceOrder.equalsIgnoreCase("desc")) {
//            orders.add(criteriaBuilder.desc(root.get("price")));
//        }
//
//        if(sortDirection != null && sortDirection.equalsIgnoreCase("asc")) {
//            orders.add(criteriaBuilder.asc(root.get("id")));
//        }else if(sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
//            orders.add(criteriaBuilder.desc(root.get("id")));
//        }
//
//        if(orders.isEmpty()) {
//            if(sortDirection != null && sortDirection.equalsIgnoreCase("asc")) {
//                orders.add(criteriaBuilder.asc(root.get("id")));
//            }else {
//                orders.add(criteriaBuilder.desc(root.get("id")));
//            }
//        }
//        return orders;
//    }
//
//    public static Specification<ProductWarehouse> hasPriceBetween(Double minPrice, Double maxPrice) {
//        return ((root, query, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            if (minPrice != null) {
//                Predicate minPricePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
//                predicates.add(minPricePredicate);
//            }
//            if (maxPrice != null) {
//                Predicate maxPricePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
//                predicates.add(maxPricePredicate);
//            }
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        });
//    }
//}
