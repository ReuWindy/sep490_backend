package com.fpt.sep490.controller;


import com.fpt.sep490.dto.*;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.DayActive;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.User;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.EmployeeService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final UserActivityService userActivityService;
    private final JwtTokenManager jwtTokenManager;

    public EmployeeController(EmployeeService employeeService, UserActivityService userActivityService, JwtTokenManager jwtTokenManager) {
        this.employeeService = employeeService;
        this.userActivityService = userActivityService;
        this.jwtTokenManager = jwtTokenManager;
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
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Employee> pagedResourcesAssembler
    ) {
        Page<Employee> employeePage = employeeService.getEmployeeByFilter(employeeCode, fullName, phoneNumber, email, pageNumber, pageSize);

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
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.createEmployee(employeeDTO);
        if (employee != null) {
            return ResponseEntity.status(HttpStatus.OK).body(employee);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateEmployee")
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(employeeDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/salary")
    public ResponseEntity<List<EmployeeWithDayActiveDTO>> getEmployeesForSalary(@RequestParam String role) {
        List<EmployeeWithDayActiveDTO> employees = employeeService.getEmployees(role.toUpperCase());
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/salary")
    public ResponseEntity<CreateSuccessResponseDTO> createEmployeeActiveDay(@RequestBody ActiveDateRequestDTO activeDateRequestDTO) {
        Employee employee = employeeService.createDayActive(activeDateRequestDTO.employeeId(), activeDateRequestDTO.dayActive(), activeDateRequestDTO.mass(), activeDateRequestDTO.note());
        CreateSuccessResponseDTO createSuccessDTO = new CreateSuccessResponseDTO("success", "Create Success", new EmployeeResponseDTO(employee.getId(), employee.getEmployeeCode(), employee.getFullName()), LocalDateTime.now());
        return new ResponseEntity<>(createSuccessDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/salary")
    public ResponseEntity<DeleteSuccessResponseDTO> deleteEmployee(@RequestParam int employeeId, @RequestParam String date) {
        employeeService.deleteDayActive(employeeId, date);
        return new ResponseEntity<>(new DeleteSuccessResponseDTO("success", "Delete Success"), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/salary")
    public ResponseEntity<CreateSuccessResponseDTO> updateEmployeeActiveDay(@RequestBody ActiveDateRequestDTO activeDateRequestDTO) {
        Employee employee = employeeService.updateDayActive(activeDateRequestDTO.employeeId(), activeDateRequestDTO.dayActive(), activeDateRequestDTO.mass(), activeDateRequestDTO.note());
        CreateSuccessResponseDTO createSuccessDTO = new CreateSuccessResponseDTO("success", "Update Success", new EmployeeResponseDTO(employee.getId(), employee.getEmployeeCode(), employee.getFullName()), LocalDateTime.now());
        return new ResponseEntity<>(createSuccessDTO, HttpStatus.OK);
    }

    @GetMapping("/day-active/{employeeId}")
    public ResponseEntity<List<DayActive>> getDayActiveByEmployeeId(@PathVariable long employeeId, @RequestParam int month, @RequestParam int year) {
        List<DayActive> dayActives = employeeService.getDayActiveByEmployeeId(employeeId, month, year);
        return new ResponseEntity<>(dayActives, HttpStatus.OK);
    }

    @GetMapping("/role")
    public ResponseEntity<List<EmployeeWithDayActiveDTO>> getEmployeesByRole(@RequestParam String role) {
        List<EmployeeWithDayActiveDTO> employees = employeeService.getEmployeesByRole(role.toUpperCase());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/daily-payroll")
    public ResponseEntity<List<DailyEmployeePayrollResponseDTO>> getPorterPayroll(@RequestParam int month, @RequestParam int year) {
        List<DailyEmployeePayrollResponseDTO> dailyEmployeePayroll = employeeService.getDailyEmployeePayroll(month, year);
        return new ResponseEntity<>(dailyEmployeePayroll, HttpStatus.OK);
    }

    @GetMapping("/monthly-payroll")
    public ResponseEntity<List<MonthlyEmployeePayrollResponseDTO>> getDriverPayroll(@RequestParam int month, @RequestParam int year) {
        List<MonthlyEmployeePayrollResponseDTO> monthlyEmployeePayroll = employeeService.getMonthlyEmployeePayroll(month, year);
        return new ResponseEntity<>(monthlyEmployeePayroll, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableEmployee(HttpServletRequest request, @PathVariable long id) {
        try {
            User employee = employeeService.deleteEmployee(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DISABLE_EMPLOYEE", "Ẩn nhân viên " + employee.getFullName() + " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(employee);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableCustomer(HttpServletRequest request, @PathVariable long id) {
        try {
            User employee = employeeService.enableEmployee(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ENABLE_EMPLOYEE", "Khôi phục nhân viên " + employee.getFullName() + " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(employee);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
