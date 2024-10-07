package com.fpt.sep490.security.dto;

import com.fpt.sep490.model.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {
    private long id;
    private String username;
    private String password;
    private UserType userType;

}
