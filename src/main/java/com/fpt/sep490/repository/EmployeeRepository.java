package com.fpt.sep490.repository;

import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

    Optional<Employee> findByEmployeeCode(String employeeCode);

}
