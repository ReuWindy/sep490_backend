package com.fpt.sep490.repository;

import com.fpt.sep490.model.Employee;

import java.util.List;

public interface EmployeeCustomRepository {
    List<Employee> getEmployees(int month, int year, String role);
}
