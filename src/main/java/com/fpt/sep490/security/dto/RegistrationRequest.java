package com.fpt.sep490.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegistrationRequest {
    @NotEmpty(message = "Tên đăng ký không được để trống!")
    private String name;
    @NotEmpty(message = "Tên đăng nhập không được để trống!")
    private String username;
    @NotEmpty(message = "Mật khẩu đăng ký không được để trống!")
    private String password;
    private String passwordConfirmation;
    private String phone;
    private boolean active = true;
    @Email(message = "Email đăng ký không hợp lệ!")
    @NotEmpty(message = "Email đăng ký không được để trống!")
    private String email;
    private String address;
    private Date dob;
    private boolean gender;
}
