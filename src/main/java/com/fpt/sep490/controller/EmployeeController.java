package com.fpt.sep490.controller;


import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (!employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(employees);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable int id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.status(HttpStatus.OK).body(employee);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployeesByCriteria(@RequestParam String searchType, @RequestParam String keyword) {
        if ("employeeCode".equals(searchType) || "bankAccountNumber".equals(searchType) || "employeeName".equals(searchType)) {
            List<Employee> searchedEmployees = employeeService.searchByCriteria(keyword);
            return ResponseEntity.status(HttpStatus.OK).body(searchedEmployees);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @PostMapping("/createEmployee")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDTO){
          Employee employee = employeeService.createEmployee(employeeDTO);
          if(employee != null){
              return ResponseEntity.status(HttpStatus.OK).body(employee);
          }
          final ApiExceptionResponse response = new ApiExceptionResponse("Create failed",HttpStatus.BAD_REQUEST,LocalDateTime.now());
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateEmployee")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee employee){
           Employee updatedEmployee = employeeService.updateEmployee(employee);
           if(updatedEmployee != null){
               return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
           }
           final ApiExceptionResponse response = new ApiExceptionResponse("Update failed",HttpStatus.BAD_REQUEST,LocalDateTime.now());
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
