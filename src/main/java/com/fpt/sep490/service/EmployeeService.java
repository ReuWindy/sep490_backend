package com.fpt.sep490.service;


import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.model.Employee;
import java.util.List;
public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
    List<Employee> searchByCriteria(String keyword);
    Employee createEmployee(EmployeeDTO employeeDTO);
    Employee updateEmployee(Employee employee);
    Employee deleteEmployee(int id);

}
