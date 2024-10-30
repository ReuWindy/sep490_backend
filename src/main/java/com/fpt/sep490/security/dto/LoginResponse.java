package com.fpt.sep490.security.dto;

import com.fpt.sep490.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserType userType;
    private String username;
    private Long userId;
}
