package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.EmployeeRole;
import com.fpt.sep490.service.EmployeeRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/employeerole")
public class EmployeeRoleController {

    private final EmployeeRoleService employeeRoleService;

    public EmployeeRoleController (EmployeeRoleService employeeRoleService){
        this.employeeRoleService = employeeRoleService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEmployeeRole() {
        List<EmployeeRole> employeeRoles = employeeRoleService.getAllEmployeeRole();
        if (!employeeRoles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(employeeRoles);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeRoleById(@PathVariable int id) {
        EmployeeRole employeeRole = employeeRoleService.getEmployeeRoleById(id);
        if (employeeRole != null) {
            return ResponseEntity.status(HttpStatus.OK).body(employeeRole);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createEmployeeRole")
    public ResponseEntity<?> createEmployeeRole(@RequestBody EmployeeRole employeeRole) {
        EmployeeRole createdEmployeeRole = employeeRoleService.createEmployeeRole(employeeRole);
        if (createdEmployeeRole != null) {

            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployeeRole);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateEmployeeRole")
    public ResponseEntity<?> updateEmployeeRole(@RequestBody EmployeeRole employeeRole) {
        EmployeeRole updatedEmployeeRole = employeeRoleService.updateEmployeeRole(employeeRole);
        if (updatedEmployeeRole != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedEmployeeRole);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
