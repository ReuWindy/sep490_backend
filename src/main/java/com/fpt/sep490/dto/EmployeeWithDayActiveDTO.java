package com.fpt.sep490.dto;

import com.fpt.sep490.model.DayActive;

import java.util.Date;
import java.util.Set;

public record EmployeeWithDayActiveDTO(
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
        Double dailyWage
) {
}
