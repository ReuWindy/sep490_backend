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
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotEmpty(message = "Tên đăng ký không được để trống!")
    private String name;
    @Email(message = "Email đăng ký không hợp lệ!")
    @NotEmpty(message = "Email đăng ký không được để trống!")
    private String email;
    @NotEmpty(message = "Tên đăng nhập không được để trống!")
    private String username;
    @NotEmpty(message = "Mật khẩu đăng ký không được để trống!")
    private String password;
    private boolean active = true;
    private String phone;
    private String address;
    private Date dateOfBirth;
    private boolean gender;
    private UserType userType;
    private long employeeRoleId;
    private String description;
    private SalaryType salaryType;
    private double dailyWage;
    private String bankName;
    private String bankNumber;
    private String image;
}

