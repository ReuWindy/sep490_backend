package com.fpt.sep490.service;

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
    private final SalaryDetailRepository salaryDetailRepository;
    private final UserTypeRepository userTypeRepository;

    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SalaryDetailRepository salaryDetailRepository, UserTypeRepository userTypeRepository, RoleRepository roleRepository){
        this.employeeRepository = employeeRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.userTypeRepository = userTypeRepository;
        this.roleRepository = roleRepository;
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
    public Page<Employee> getEmployeeByFilter(String employeeCode, String employeeName, String phoneNumber, int pageNumber, int pageSize) {
           try{
               Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
               Specification<Employee> specification = EmployeeSpecification.hasEmployeeCodeOrEmployeeNameOrPhoneNumber(employeeCode, employeeName, phoneNumber);
               return employeeRepository.findAll(specification,pageable);
           }catch (Exception e){
               return null;
           }
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

        // Tìm UserType dựa vào id
        UserType userType = userTypeRepository.findById(employeeDTO.getUserTypeId()).orElseThrow(() -> new RuntimeException("UserType not found"));
        employee.setUserType(userType);

        // set các thuộc tính của Employee
        employee.setEmployeeCode(RandomEmployeeCodeGenerator.generateEmployeeCode());
        employee.setEmployeeName(employeeDTO.getEmployeeName());
        employee.setJoinDate(new Date());
        employee.setBankName(employeeDTO.getBankName());
        employee.setBankNumber(employeeDTO.getBankNumber());

        // Tìm Role dựa trên id
        Role role = roleRepository.findById(employeeDTO.getRoleId()).orElseThrow(()-> new RuntimeException("Role not found"));
        employee.setRole(role);

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
