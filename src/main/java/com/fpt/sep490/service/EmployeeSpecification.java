package com.fpt.sep490.service;

import com.fpt.sep490.model.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> hasEmployeeCodeOrFullNameOrPhoneNumber(String employeeCode, String fullName, String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (employeeCode != null && !employeeCode.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + employeeCode + "%"));
            }
            if (fullName != null && !fullName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + fullName + "%"));
            }
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + phoneNumber + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
