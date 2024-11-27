package com.fpt.sep490.security.dto;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.model.EmployeeRole;
import com.fpt.sep490.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "Tên đăng nhập không được bỏ trống")
    private String name;
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được bỏ trống")
    private String email;
    @NotBlank(message = "Tên đăng nhập không được bỏ trống")
    private String username;
    @NotBlank(message = "Mật khẩu không được bỏ trống")
    private String password;
    private boolean active = true;
    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^(\\+84|0)[3-9]{1}[0-9]{8}$", message = "Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10 hoặc 11 chữ số")
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

