package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.LoginErrorResponse;
import com.fpt.sep490.security.dto.LoginRequest;
import com.fpt.sep490.security.dto.LoginResponse;
import com.fpt.sep490.security.jwt.JwtTokenService;
import io.github.resilience4j.ratelimiter.RateLimiter;
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
    private final RateLimiter rateLimiter;

    @PostMapping("/loginRequest")
    public ResponseEntity<Object> loginRequest(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        if (!rateLimiter.acquirePermission()) {
            LoginErrorResponse errorResponse = new LoginErrorResponse("Quá nhiều yêu cầu. Vui lòng thử lại sau.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }

        try {
            final LoginResponse loginResponse = jwtTokenService.getLoginResponse(loginRequest, response);
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException ex) {
            LoginErrorResponse errorResponse = new LoginErrorResponse("Thông tin đăng nhập sai. Vui lòng kiểm tra lại tài khoản và mật khẩu.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception ex) {
            LoginErrorResponse errorResponse = new LoginErrorResponse("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}