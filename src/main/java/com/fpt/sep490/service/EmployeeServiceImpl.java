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
    private final UserTypeRepository userTypeRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeRoleRepository employeeRoleRepository, UserTypeRepository userTypeRepository, RoleRepository roleRepository, SalaryDetailRepository salaryDetailRepository){
        this.employeeRepository = employeeRepository;
        this.employeeRoleRepository = employeeRoleRepository;
        this.userTypeRepository = userTypeRepository;
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

        // Find UserType by id
        UserType userType = userTypeRepository.findById(employeeDTO.getUserTypeId()).orElseThrow(() -> new RuntimeException("UserType not found"));

        // Find employeeRole by id
        EmployeeRole employeeRole = employeeRoleRepository.findById(employeeDTO.getEmployeeRoleId()).orElseThrow(() ->new RuntimeException("Employee Role not found"));

        // set attributes of salaryDetail
        SalaryDetail salaryDetail = new SalaryDetail();
        salaryDetail.setSalaryType(employeeDTO.getSalaryType());
        salaryDetail.setDailyWage(employeeDTO.getDailyWage());
        salaryDetail.setDaysWorked(0);
        salaryDetail.setMonthlySalary(0);

        // set attributes of Role
        Role role = new Role();
        role.setSalaryDetail(salaryDetail);
        role.setEmployeeRole(employeeRole);
        role.setDescription(employeeDTO.getDescription());


        // st attributes of User
        Employee employee = new Employee();
        employee.setFullName(employeeDTO.getFullName());
        employee.setUsername(employeeDTO.getUsername());
        employee.setPassword(employeeDTO.getPassword());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setEmail(employeeDTO.getEmail());
        employee.setAddress(employeeDTO.getAddress());
        employee.setCreateAt(new Date());
        employee.setUpdateAt(new Date());
        employee.setActive(true);
        employee.setUserType(userType);

        // set các thuộc tính của Employee
        employee.setEmployeeCode(RandomEmployeeCodeGenerator.generateEmployeeCode());
        employee.setJoinDate(new Date());
        employee.setBankName(employeeDTO.getBankName());
        employee.setBankNumber(employeeDTO.getBankNumber());
        employee.setRole(role);

        salaryDetailRepository.save(salaryDetail);
        roleRepository.save(role);
        employeeRepository.save(employee);
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        Employee existingEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        if(existingEmployee != null){
            existingEmployee.setBankName(employee.getBankName());
            existingEmployee.setBankNumber(employee.getBankNumber());
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
