package com.fpt.sep490.service;

import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.dto.EmployeeWithDayActiveDTO;
import com.fpt.sep490.exceptions.ApiRequestException;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final SalaryDetailRepository salaryDetailRepository;
    private final EmployeeCustomRepository employeeCustomRepository;

    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeRoleRepository employeeRoleRepository, RoleRepository roleRepository, SalaryDetailRepository salaryDetailRepository, EmployeeCustomRepository employeeCustomRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeRoleRepository = employeeRoleRepository;
        this.roleRepository = roleRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.employeeCustomRepository = employeeCustomRepository;
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
            EmployeeRole newEmployeeRole = employeeRoleRepository.findById(employee.getEmployeeRoleId()).orElse(null);
            if(newEmployeeRole != null) {
                existingEmployee.setFullName(employee.getFullName());
                existingEmployee.setEmail(employee.getEmail());
                existingEmployee.setPhone(employee.getPhone());
                existingEmployee.setAddress(employee.getAddress());
                existingEmployee.setBankName(employee.getBankName());
                existingEmployee.setBankNumber(employee.getBankNumber());
                existingEmployee.setDob(employee.getDob());
                existingEmployee.setGender(employee.isGender());
                existingEmployee.setImage(employee.getImage());
                existingEmployee.setUpdateAt(new Date());
                Role role = existingEmployee.getRole();
                if(role != null){
                    role.setEmployeeRole(newEmployeeRole);
                }
                employeeRepository.save(existingEmployee);
                return existingEmployee;
            }
        }
        return null;
    }

    @Override
    public Employee deleteEmployee(int id) {
        return null;
    }

    @Override
    public List<EmployeeWithDayActiveDTO> getEmployees(int month, int year, String role) {
        if (!Objects.equals(role, com.fpt.sep490.Enum.EmployeeRole.DRIVER.toString())
                && !Objects.equals(role, com.fpt.sep490.Enum.EmployeeRole.PORTER.toString())) {
            throw new ApiRequestException("Invalid role");
        }
        List<Employee> employees = employeeCustomRepository.getEmployees(month, year, role);
        return convertEmployeeToEmployeeWithDayActiveDTO(employees);
    }

    @Transactional
    @Override
    public Employee createDayActive(long id, String date, int mass, String note) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dayActive = inputFormat.parse(date);
            String formattedDateString = dateFormat.format(dayActive);
            Date formattedDate = dateFormat.parse(formattedDateString);
            employeeCustomRepository.createActiveDate(id,formattedDate,mass,note);
            return employeeCustomRepository.getEmployeeById(id);
        } catch (ParseException e) {
            throw new ApiRequestException("Invalid date format");
        }
    }

    @Transactional
    @Override
    public void deleteDayActive(long id, String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dayActive = inputFormat.parse(date);
            String formattedDateString = dateFormat.format(dayActive);
            Date formattedDate = dateFormat.parse(formattedDateString);
            employeeCustomRepository.deleteActiveDate(id,formattedDate);
        } catch (ParseException e) {
            throw new ApiRequestException("Invalid date format");
        }
    }

    @Transactional
    @Override
    public Employee updateDayActive(long id, String date, int mass, String note) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dayActive = inputFormat.parse(date);
            String formattedDateString = dateFormat.format(dayActive);
            Date formattedDate = dateFormat.parse(formattedDateString);
            return employeeCustomRepository.updateActiveDate(id,formattedDate,mass,note);
        } catch (ParseException e) {
            throw new ApiRequestException("Invalid date format");
        }
    }

    @Override
    public List<DayActive> getDayActiveByEmployeeId(long id, int month, int year) {
        return employeeCustomRepository.getDayActiveByEmployeeId(id,month,year);
    }

    @Override
    public List<EmployeeWithDayActiveDTO> getEmployeesByRole(String role) {
        if (!Objects.equals(role, com.fpt.sep490.Enum.EmployeeRole.DRIVER.toString())
                && !Objects.equals(role, com.fpt.sep490.Enum.EmployeeRole.PORTER.toString())) {
            throw new ApiRequestException("Invalid role");
        }
        List<Employee> employees = employeeCustomRepository.getEmployeesByRole(role);
        return convertEmployeeToEmployeeWithDayActiveDTO(employees);
    }

    public List<EmployeeWithDayActiveDTO> convertEmployeeToEmployeeWithDayActiveDTO(List<Employee> employees){
        if(employees.isEmpty()){
            return List.of();
        }
        return employees.stream().map(
                employee -> new EmployeeWithDayActiveDTO(employee.getId(), employee.getPhone(), employee.getEmail(), employee.getAddress(),
                        employee.getFullName(), employee.getBankName(), employee.getBankNumber(), employee.getDob(),
                        employee.isGender(), employee.getImage(), employee.getEmployeeRole().toString(),
                        employee.getDailyWage())
        ).toList();
    }
}
