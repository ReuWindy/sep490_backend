package com.fpt.sep490.service;


import com.fpt.sep490.model.Employee;
import java.util.List;
public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
    Employee getEmployeeByBankDetail(String bankDetail);
    Employee createEmployee(Employee employee);
    Employee updateEmployee(Employee employee);
    Employee deleteEmployee(int id);

}
