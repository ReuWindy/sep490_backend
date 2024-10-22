package com.fpt.sep490.service;

import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.UserType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ContractSpecification {
    public static Specification<Contract> hasContractNumberOrName(String contractNumber, String name){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (contractNumber != null && !contractNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("contractNumber"), "%" + contractNumber + "%"));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("customer").get("name"), "%" + name + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
