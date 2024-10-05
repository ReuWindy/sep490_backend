package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.SalaryType;
import lombok.Data;

@Data
public class EmployeeDTO {

    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String address;
    private String employeeName;
    private String bankName;
    private String bankNumber;
    private SalaryType salaryType;
    private double dailyWage;
    private long roleId;
    private long userTypeId;
}
