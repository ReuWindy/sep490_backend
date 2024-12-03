package com.fpt.sep490.security.dto;

import com.fpt.sep490.model.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeLoginResponse extends LoginResponse{
    private String employeeRole;

    public EmployeeLoginResponse(String token, UserType userType, String username, Long userId, String employeeRole) {
        super(token, userType, username, userId);
        this.employeeRole = employeeRole;
    }
}
