package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.model.WarehouseReceipt;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WarehouseReceiptSpecification {

    public static Specification<WarehouseReceipt> hasType(ReceiptType receiptType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (receiptType != null) {
                predicates.add(criteriaBuilder.equal(root.get("receiptType"), receiptType));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<WarehouseReceipt> hasUsername(String username) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (username != null) {
                predicates.add(cb.equal(root.get("batch").get("batchCreator").get("username"), username));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<WarehouseReceipt> isReceiptDateBetween(Date startDate, Date endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("receiptDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("receiptDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("receiptDate"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
