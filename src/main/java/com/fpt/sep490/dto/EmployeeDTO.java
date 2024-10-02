package com.fpt.sep490.dto;

import com.fpt.sep490.model.SalaryType;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDTO {

    private String username;
    private String password;
    private long phoneNumber;
    private String email;
    private String address;
    private String employeeName;
    private String bankName;
    private String bankNumber;
    private SalaryType salaryType;
    private double dailyWage;
}
