package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.LogoutResponse;
import com.fpt.sep490.security.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/logout")
public class LogoutController {
    private final JwtTokenService jwtTokenService;

    @PostMapping("/logoutRequest")
    public ResponseEntity<LogoutResponse> logoutRequest(HttpServletRequest request, HttpServletResponse response) {
        LogoutResponse logoutResponse = jwtTokenService.getLogoutResponse(request, response);
        return new ResponseEntity<>(logoutResponse, HttpStatus.OK);
    }
}
