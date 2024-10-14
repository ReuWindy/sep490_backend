package com.fpt.sep490.service;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.utils.RandomEmployeeCodeGenerator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeRoleRepository employeeRoleRepository, RoleRepository roleRepository, SalaryDetailRepository salaryDetailRepository){
        this.employeeRepository = employeeRepository;
        this.employeeRoleRepository = employeeRoleRepository;
        this.roleRepository = roleRepository;
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
    public Page<Employee> getEmployeeByFilter(String employeeCode, String fullName, String phoneNumber, int pageNumber, int pageSize) {
           try{
               Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
               Specification<Employee> specification = EmployeeSpecification.hasEmployeeCodeOrFullNameOrPhoneNumber(employeeCode, fullName, phoneNumber);
               return employeeRepository.findAll(specification,pageable);
           }catch (Exception e){
               return null;
           }
    }

    @Override
    public Employee createEmployee(EmployeeDTO employeeDTO) {
        return null;
    }

    @Override
    public Employee updateEmployee(EmployeeDTO employee) {
        Employee existingEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        if(existingEmployee != null){
            existingEmployee.setFullName(employee.getFullName());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setPhone(employee.getPhone());
            existingEmployee.setAddress(employee.getAddress());
            existingEmployee.setBankName(employee.getBankName());
            existingEmployee.setBankNumber(employee.getBankNumber());
            existingEmployee.setDob(employee.getDob());
            existingEmployee.setGender(employee.isGender());
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
