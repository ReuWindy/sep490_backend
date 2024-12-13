package com.fpt.sep490.repository;

import com.fpt.sep490.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

    List<Employee> findAllByActive(Boolean active);

    Optional<Employee> findByUsername(String username);
}