package com.fpt.sep490.security.jwt;

import com.fpt.sep490.model.User;
import com.fpt.sep490.security.dto.AuthenticatedUserDto;
import com.fpt.sep490.security.dto.LoginRequest;
import com.fpt.sep490.security.dto.LoginResponse;
import com.fpt.sep490.security.mapper.UserMapper;
import com.fpt.sep490.security.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    final int cookieExpirationDuration = 7*24*60*60*1000;

    public LoginResponse getLoginResponse(LoginRequest loginRequest, HttpServletResponse response) {

        final String username = loginRequest.getUsername();
        final String password = loginRequest.getPassword();

        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final AuthenticatedUserDto authenticatedUserDto = userService.findAuthenticatedUserByUsername(username);

        final User user = UserMapper.INSTANCE.convertToUser(authenticatedUserDto);
        final String token = jwtTokenManager.generateToken(user);
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(cookieExpirationDuration);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        //log.info("Cookie details: Name = {}, Value = {}, MaxAge = {}, HttpOnly = {}, Secure = {}, Path = {}", cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.isHttpOnly(), cookie.getSecure(), cookie.getPath());
        log.info("{} has successfully logged in!", user.getUsername());

        return new LoginResponse(token, user.getUserType(), user.getUsername());
    }
}
