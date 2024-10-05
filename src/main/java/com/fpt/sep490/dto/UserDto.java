package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String phone;
    private String username;
    private String password;
    private String email;
    private String userType;
    private String address;
}
