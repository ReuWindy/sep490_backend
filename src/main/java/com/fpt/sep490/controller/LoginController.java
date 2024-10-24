package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.security.dto.LoginPhoneRequest;
import com.fpt.sep490.security.dto.LoginRequest;
import com.fpt.sep490.security.dto.LoginResponse;
import com.fpt.sep490.security.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final JwtTokenService jwtTokenService;
    @PostMapping("/loginRequest")
    public ResponseEntity<LoginResponse> loginRequest(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        final LoginResponse loginResponse = jwtTokenService.getLoginResponse(loginRequest, response);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/phone")
    public ResponseEntity<?> loginRequest(@RequestBody LoginPhoneRequest loginPhoneRequest, HttpServletResponse response) {

        final LoginResponse loginResponse = jwtTokenService.getLoginPhoneResponse(loginPhoneRequest, response);

        if(loginResponse != null) {
            return ResponseEntity.ok(loginResponse);
        }
        final ApiExceptionResponse exResponse = new ApiExceptionResponse("ERROR", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exResponse);
    }

}
