package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.SalaryType;
import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDTO {
    private long id;
    private String phone;
    private String email;
    private String address;
    private String fullName;
    private String bankName;
    private String bankNumber;
    private Date dob;
    private boolean gender;
    private String image;
    private Long employeeRoleId;
    private double dailyWage;
}
