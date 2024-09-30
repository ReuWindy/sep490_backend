package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecification implements Specification<Supplier> {
    @Override
    public Predicate toPredicate(Root<Supplier> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    @Override
    public Specification<Supplier> and(Specification<Supplier> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<Supplier> or(Specification<Supplier> other) {
        return Specification.super.or(other);
    }
}
