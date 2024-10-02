package com.fpt.sep490.controller;


import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (!employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(employees);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<Employee>>> getAllEmployeesByFilter(
            @RequestParam(required = false) String employeeCode,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Employee> pagedResourcesAssembler
    ){
        Page<Employee> employeePage = employeeService.getEmployeeByFilter(employeeCode, employeeName, phoneNumber, pageNumber, pageSize);

        PagedModel<EntityModel<Employee>> pagedModel = pagedResourcesAssembler.toModel(employeePage);

        return ResponseEntity.ok(pagedModel);
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
