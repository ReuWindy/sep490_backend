package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.LoginErrorResponse;
import com.fpt.sep490.security.dto.LoginRequest;
import com.fpt.sep490.security.dto.LoginResponse;
import com.fpt.sep490.security.jwt.JwtTokenService;
import com.fpt.sep490.service.RedisRateLimiter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final JwtTokenService jwtTokenService;
    private final RedisRateLimiter redisRateLimiter;

    @PostMapping("/loginRequest")
    public ResponseEntity<Object> loginRequest(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        String clientId = loginRequest.getUsername();
        String endpoint = "/login/loginRequest";
        int limit = 3;
        int timeWindowSeconds = 60;

//        if (!redisRateLimiter.isAllowed(clientId, endpoint, limit, timeWindowSeconds)) {
//            LoginErrorResponse errorResponse = new LoginErrorResponse("Thư giãn chút, quá số lần yêu cầu.");
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
//        }

        try {
            final LoginResponse loginResponse = jwtTokenService.getLoginResponse(loginRequest, response);
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException ex) {
            LoginErrorResponse errorResponse = new LoginErrorResponse("Thông tin đăng nhập sai.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}