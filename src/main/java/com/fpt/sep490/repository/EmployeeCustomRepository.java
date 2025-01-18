package com.fpt.sep490.repository;

import com.fpt.sep490.model.DayActive;
import com.fpt.sep490.model.Employee;

import java.util.Date;
import java.util.List;

public interface EmployeeCustomRepository {
    Employee getEmployeeById(long id);

    List<Employee> getEmployees(String role);

    void createActiveDate(long id, Date date, double mass, String note);

    void deleteActiveDate(long id, Date date);

    Employee updateActiveDate(long id, Date date, double mass, String note);

    List<DayActive> getDayActiveByEmployeeId(long id, int month, int year);

    List<Employee> getEmployeesByRole(String role);
}