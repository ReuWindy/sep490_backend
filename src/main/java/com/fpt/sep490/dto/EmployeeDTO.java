package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.SalaryType;
import lombok.Data;

@Data
public class EmployeeDTO {

    private String username;
    private String password;
    private Long phoneNumber;
    private String email;
    private String address;
    private String fullName;
    private String bankName;
    private String bankNumber;
    private SalaryType salaryType;
    private Double dailyWage;
    private Long userTypeId;
    private Long employeeRoleId;
    private String description;
}
