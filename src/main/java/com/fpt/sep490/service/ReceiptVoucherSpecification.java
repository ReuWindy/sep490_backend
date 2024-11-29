package com.fpt.sep490.service;

import com.fpt.sep490.model.ReceiptVoucher;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceiptVoucherSpecification {

    public static Specification<ReceiptVoucher> isReceiptDateBetween(Date startDate, Date endDate, String incomeCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (incomeCode != null) {
                predicates.add(cb.like(root.get("receiptCode"), "%" + incomeCode + "%"));
            }

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
