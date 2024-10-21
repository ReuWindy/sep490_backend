package com.fpt.sep490.controller;

import com.fpt.sep490.dto.ForgotRequest;
import com.fpt.sep490.dto.PasswordRequest;
import com.fpt.sep490.dto.UserDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.User;
import com.fpt.sep490.security.dto.*;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.security.jwt.JwtTokenService;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final JwtTokenManager jwtTokenManager;


    public UserController(UserService userService, JwtTokenService jwtTokenService, JwtTokenManager jwtTokenManager) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.jwtTokenManager = jwtTokenManager;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getAllUser() {
        final List<User> listUser = userService.listAllUser();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : listUser) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setUsername(user.getUsername());
            dto.setUserType(user.getUserType().name());
            dto.setAddress(user.getAddress());
            userDtos.add(dto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<?> getUserByUserName(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user != null) {

            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setUsername(user.getUsername());
            dto.setUserType(user.getUserType().name());
            dto.setAddress(user.getAddress());
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } else {
            final ApiExceptionResponse response = new ApiExceptionResponse("User not found!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<ApiExceptionResponse> deleteUserByUserName(@PathVariable String username) {
        User user = userService.findByUsername(username);
        ApiExceptionResponse response;
        if (user != null) {
            if (!user.getUserType().name().equals("ROLE_ADMIN")) {
                boolean isDelete = userService.deleteUserByUserName(username);
                if (isDelete) {
                    response = new ApiExceptionResponse("Delete user successfully!", HttpStatus.OK, LocalDateTime.now());
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            } else {
                response = new ApiExceptionResponse("Can't delete admin!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        response = new ApiExceptionResponse("User not found!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/edit/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserDto userDto) {
        ApiExceptionResponse response;
        User u = userService.updateUserByUserName(username, userDto);
        if (u != null) {
            response = new ApiExceptionResponse("Update user successfully!", HttpStatus.OK, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response = new ApiExceptionResponse("User not found!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/profileEdit/{token}")
    public ResponseEntity<?> profileEdit(@PathVariable String token, @RequestBody UserDto userDto) {
        User u = userService.updateUserProfile(token, userDto);
        if(u != null) {
            return  ResponseEntity.status(HttpStatus.OK).body(null);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("User not found!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/passwordEdit/{token}")
    public ResponseEntity<?> profileEdit(@PathVariable String token, @RequestBody PasswordRequest request, HttpServletResponse response) {
        String username = jwtTokenManager.getUsernameFromToken(token);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(request.getOldpass());
        LoginResponse loginResponse = jwtTokenService.getLoginResponse(loginRequest, response);
        if(loginResponse != null) {
            User u = userService.changePassword(token, request);
            if(u != null) {
                return  ResponseEntity.status(HttpStatus.OK).body(null);
            }
        }
        ApiExceptionResponse responseAPI = new ApiExceptionResponse("User not found!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseAPI);
    }
    @PostMapping("/create")
    public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody CreateUserRequest createUserRequest) {
        RegistrationRequest request = new RegistrationRequest();
        request.setName(createUserRequest.getName());
        request.setEmail(createUserRequest.getEmail());
        request.setPhone(createUserRequest.getPhone());
        request.setUsername(createUserRequest.getUsername());
        request.setAddress(createUserRequest.getAddress());
        request.setPassword(createUserRequest.getPassword());
        request.setDateOfBirth(createUserRequest.getDateOfBirth());
        request.setGender(createUserRequest.isGender());
        final RegistrationResponse registrationResponse = userService.createUserByAdmin(request, createUserRequest.getUserType(), createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponse);
    }

    @PostMapping("/loss-pass")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotRequest forgotRequest) {
        boolean sent = userService.sendPasswordToEmail(forgotRequest.getEmail());
        if (sent) {
            final ApiSuccessResponse response = new ApiSuccessResponse("Successful", HttpStatus.OK, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Successful", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
