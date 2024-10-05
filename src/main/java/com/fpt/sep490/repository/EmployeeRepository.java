package com.fpt.sep490.repository;

import com.fpt.sep490.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e from Employee e WHERE e.employeeCode LIKE :keyword OR e.phone = :keyword OR e.employeeName LIKE :keyword")
    List<Employee> searchByKeyword(@Param("keyword") String keyword);

    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

}
