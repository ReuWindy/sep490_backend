package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.LoginRequest;
import com.fpt.sep490.security.dto.LoginResponse;
import com.fpt.sep490.security.jwt.JwtTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final JwtTokenService jwtTokenService;
    @PostMapping("/loginRequest")
    public ResponseEntity<LoginResponse> loginRequest(@Valid @RequestBody LoginRequest loginRequest) {

        final LoginResponse loginResponse = jwtTokenService.getLoginResponse(loginRequest);

        return ResponseEntity.ok(loginResponse);
    }
}