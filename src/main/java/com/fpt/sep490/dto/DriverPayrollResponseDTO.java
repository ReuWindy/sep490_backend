package com.fpt.sep490.dto;

import java.util.Date;

public record DriverPayrollResponseDTO(
        long id,
        String phone,
        String email,
        String address,
        String fullName,
        String bankName,
        String bankNumber,
        Date dob,
        boolean gender,
        String image,
        String employeeRole,
        double dailyWage,
        int dayWorked,
        double totalSalary
) {
}
