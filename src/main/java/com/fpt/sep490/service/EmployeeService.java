package com.fpt.sep490.service;


import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.model.Employee;
import java.util.List;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
    Page<Employee> getEmployeeByFilter(String employeeCode, String employeeName, String phoneNumber, int pageNumber, int pageSize);
    Employee createEmployee(EmployeeDTO employeeDTO);
    Employee updateEmployee(Employee employee);
    Employee deleteEmployee(int id);

}
