package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String image;
    private String phone;
    private String username;
    private String password;
    private Date dob;
    private String createdAt;
    private String email;
    private String userType;
    private String address;
}
