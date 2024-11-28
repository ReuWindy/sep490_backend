package com.fpt.sep490.security.service;

import com.fpt.sep490.dto.PasswordRequest;
import com.fpt.sep490.dto.UserDto;
import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import com.fpt.sep490.security.dto.AuthenticatedUserDto;
import com.fpt.sep490.security.dto.CreateUserRequest;
import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    List<User> listAllUser();
    boolean deleteUserByUserName(String username);
    User updateUserByUserName(String username, UserDto userDto);
    User updateUserProfile(String token, UserDto userDto);
    User changePassword(String token, PasswordRequest request);
    RegistrationResponse registration(RegistrationRequest registrationRequest);
    RegistrationResponse createUserByAdmin(RegistrationRequest registrationRequest, UserType userType, CreateUserRequest createUserRequest);
    AuthenticatedUserDto findAuthenticatedUserByUsername(String username);
    boolean sendPasswordToEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
}
