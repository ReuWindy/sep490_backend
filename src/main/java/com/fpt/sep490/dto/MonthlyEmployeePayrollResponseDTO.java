package com.fpt.sep490.dto;

import org.apache.xpath.operations.Bool;

import java.util.Date;

public record MonthlyEmployeePayrollResponseDTO(
        Long id,
        String phone,
        String email,
        String address,
        String fullName,
        String bankName,
        String bankNumber,
        Date dob,
        Boolean gender,
        String image,
        String employeeRole,
        Double dailyWage,
        Integer dayWorked,
        Double unpaidSalary,
        Double totalSalary
) {
}
