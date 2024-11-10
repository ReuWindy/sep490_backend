package com.fpt.sep490.security.dto;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.model.EmployeeRole;
import com.fpt.sep490.model.UserType;
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
    @NotEmpty(message = "{registration_name_not_empty}")
    private String name;
    @NotEmpty(message = "{registration_username_not_empty}")
    private String username;
    @NotEmpty(message = "{registration_password_not_empty}")
    private String password;
    private String passwordConfirmation;
    private String phone;
    private boolean active = true;
    @Email(message = "{registration_email_is_not_valid}")
    @NotEmpty(message = "{registration_email_not_empty}")
    private String email;
    private String address;
    private Date dob;
    private boolean gender;
}
