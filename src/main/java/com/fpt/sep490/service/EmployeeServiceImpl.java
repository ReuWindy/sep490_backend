package com.fpt.sep490.service;

import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.EmployeeRepository;
import com.fpt.sep490.repository.SalaryDetailRepository;
import com.fpt.sep490.utils.RandomEmployeeCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SalaryDetailRepository salaryDetailRepository){
        this.employeeRepository = employeeRepository;
        this.salaryDetailRepository = salaryDetailRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        Optional<Employee> employee = employeeRepository.findById((long) id);
        return employee.orElse(null);
    }

    @Override
    public List<Employee> searchByCriteria(String keyword) {
        if(keyword == null || keyword.trim().isEmpty()){
            return Collections.emptyList();
        }
        return employeeRepository.searchByKeyword(keyword);
    }

    @Override
    public Employee createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // set các thuộc tính của User
        employee.setUsername(employeeDTO.getUsername());
        employee.setPassword(employeeDTO.getPassword());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setEmail(employeeDTO.getEmail());
        employee.setAddress(employeeDTO.getAddress());
        employee.setCreateAt(new Date());
        employee.setUpdateAt(new Date());
        employee.setActive(true);
        // set các thuộc tính của Employee
        employee.setEmployeeCode(RandomEmployeeCodeGenerator.generateEmployeeCode());
        employee.setEmployeeName(employeeDTO.getEmployeeName());
        employee.setJoinDate(new Date());
        employee.setBankName(employeeDTO.getBankName());
        employee.setBankNumber(employeeDTO.getBankNumber());
        // Lưu employee để có id
        Employee savedEmployee = employeeRepository.save(employee);

        // set các thuộc tính của SalaryDetail
        SalaryDetail salaryDetail = new SalaryDetail();
        salaryDetail.setSalaryType(employeeDTO.getSalaryType());
        salaryDetail.setDailyWage(employeeDTO.getDailyWage());
        salaryDetail.setDaysWorked(0);
        salaryDetail.setMonthlySalary(0);
        salaryDetail.setEmployee(savedEmployee);
        salaryDetailRepository.save(salaryDetail);

        return savedEmployee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        Employee existingEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        if(existingEmployee != null){
            existingEmployee.setEmployeeName(employee.getEmployeeName());
            existingEmployee.setBankName(employee.getBankName());
            existingEmployee.setBankNumber(employee.getBankNumber());
            existingEmployee.setActive(employee.isActive());
            employeeRepository.save(existingEmployee);
            return existingEmployee;
        }
        return null;
    }

    @Override
    public Employee deleteEmployee(int id) {
        return null;
    }
}
