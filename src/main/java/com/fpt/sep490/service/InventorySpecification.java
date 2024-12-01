package com.fpt.sep490.service;

import com.fpt.sep490.model.Inventory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventorySpecification {

    public static Specification<Inventory> hasCode(String code) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (code != null) {
                predicates.add(cb.like(root.get("inventoryCode"), "%" + code + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Inventory> isInventoryDateBetween(Date startDate, Date endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("inventoryDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("inventoryDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("inventoryDate"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
