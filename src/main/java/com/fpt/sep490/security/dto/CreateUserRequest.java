package com.fpt.sep490.security.dto;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.model.EmployeeRole;
import com.fpt.sep490.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateUserRequest {
    @NotEmpty(message = "{registration_name_not_empty}")
    private String name;
    @Email(message = "{registration_email_is_not_valid}")
    @NotEmpty(message = "{registration_email_not_empty}")
    private String email;
    @NotEmpty(message = "{registration_username_not_empty}")
    private String username;
    @NotEmpty(message = "{registration_password_not_empty}")
    private String password;
    private String phone;
    private String address;
    private Date dateOfBirth;
    private UserType userType;
    private long employeeRoleId;
    private String description;
    private SalaryType salaryType;
    private double dailyWage;
    private String bankName;
    private String bankNumber;
}

