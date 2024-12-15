package com.fpt.sep490.security.jwt;

import com.fpt.sep490.model.Employee;
import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import com.fpt.sep490.security.dto.*;
import com.fpt.sep490.security.mapper.UserMapper;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.service.EmployeeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final EmployeeService employeeService;

    @Value("${app.cookie.domain}")
    private String cookieDomain;

    public LoginResponse getLoginResponse(LoginRequest loginRequest, HttpServletResponse response) {

        final String username = loginRequest.getUsername();
        final String password = loginRequest.getPassword();
        try {
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }catch (AuthenticationException e){
            log.error("Xác thực thất bại cho người dùng: {}", username, e);
            throw new RuntimeException("Lỗi: tên đăng nhập hoặc mật khẩu không đúng! ", e);
        }

        final AuthenticatedUserDto authenticatedUserDto;
        try{
            authenticatedUserDto= userService.findAuthenticatedUserByUsername(username);
        }catch(NoSuchElementException e){
            log.error("Không tìm thấy người dùng: {}", username, e);
            throw new RuntimeException("Lỗi: Không tìm thấy người dùng! ", e);
        }


        final User user = UserMapper.INSTANCE.convertToUser(authenticatedUserDto);
        final Employee employee = employeeService.getEmployeeById((int) user.getId());
        final String token = jwtTokenManager.generateToken(user);
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(604800);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.setHeader("Set-Cookie", "token=" + token + "; Path=/; Domain=" + cookieDomain + "; Max-Age=604800; HttpOnly; Secure; SameSite=None"); // ## For production
        log.info("{} has successfully logged in!", user.getUsername());
        if(user.getUserType() == UserType.ROLE_EMPLOYEE){
            return new EmployeeLoginResponse(token, user.getUserType(), user.getUsername(), user.getId(), employee.getRole().getEmployeeRole().getRoleName());
        }
        return new LoginResponse(token, user.getUserType(), user.getUsername(), user.getId());
    }

    public LogoutResponse getLogoutResponse(HttpServletRequest request, HttpServletResponse response) {
        String token = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            return new LogoutResponse("Invalid logout request, no token found in cookies.");
        }

        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.setHeader("Set-Cookie", "token=" + token + "; Path=/; Domain=" + cookieDomain + "; Max-Age=0; HttpOnly; Secure; SameSite=None"); // ## For production
        log.info("User has successfully logged out!");

        return new LogoutResponse("You have logged out successfully.");
    }
}
