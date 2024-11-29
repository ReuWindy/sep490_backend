package com.fpt.sep490.service;


import com.fpt.sep490.dto.DailyEmployeePayrollResponseDTO;
import com.fpt.sep490.dto.EmployeeDTO;
import com.fpt.sep490.dto.EmployeeWithDayActiveDTO;
import com.fpt.sep490.dto.MonthlyEmployeePayrollResponseDTO;
import com.fpt.sep490.model.DayActive;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.User;
import org.springframework.data.domain.Page;
import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee getEmployeeById(int id);

    Page<Employee> getEmployeeByFilter(String employeeCode, String fullName, String phoneNumber, String email, int pageNumber, int pageSize);

    Employee createEmployee(EmployeeDTO employeeDTO);

    Employee updateEmployee(EmployeeDTO employee);

    User deleteEmployee(Long id);

    User enableEmployee(Long id);

    List<EmployeeWithDayActiveDTO> getEmployees(String role);

    Employee createDayActive(long id, String date, int mass, String note);

    void deleteDayActive(long id, String date);

    Employee updateDayActive(long id, String date, int mass, String note);

    List<DayActive> getDayActiveByEmployeeId(long id, int month, int year);

    List<EmployeeWithDayActiveDTO> getEmployeesByRole(String role);

    List<DailyEmployeePayrollResponseDTO> getDailyEmployeePayroll(int month, int year);

    List<MonthlyEmployeePayrollResponseDTO> getMonthlyEmployeePayroll(int month, int year);
}
