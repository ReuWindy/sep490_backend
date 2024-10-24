package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.model.WarehouseReceipt;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WarehouseReceiptSpecification {
    public static Specification<WarehouseReceipt> hasImportDateOrType(Date importDate, ReceiptType receiptType) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (importDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("importDate"), importDate));
            }
            if (receiptType != null) {
                predicates.add(criteriaBuilder.equal(root.get("receiptType"), receiptType));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
