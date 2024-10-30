package com.fpt.sep490.security.jwt;

import com.fpt.sep490.model.User;
import com.fpt.sep490.security.dto.*;
import com.fpt.sep490.security.mapper.UserMapper;
import com.fpt.sep490.security.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public LoginResponse getLoginResponse(LoginRequest loginRequest, HttpServletResponse response) {

        final String username = loginRequest.getUsername();
        final String password = loginRequest.getPassword();

        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final AuthenticatedUserDto authenticatedUserDto = userService.findAuthenticatedUserByUsername(username);

        final User user = UserMapper.INSTANCE.convertToUser(authenticatedUserDto);
        final String token = jwtTokenManager.generateToken(user);
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(604800);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        log.info("{} has successfully logged in!", user.getUsername());

        return new LoginResponse(token, user.getUserType(), user.getUsername(),user.getId());
    }

    public LoginResponse getLoginPhoneResponse(LoginPhoneRequest loginPhoneRequest, HttpServletResponse response) {
        try {
            FirebaseToken isValid = jwtTokenManager.verifyIdToken(loginPhoneRequest.getIdToken());

            if(isValid != null) {
                String phoneNumber = isValid.getClaims().get("phone_number").toString();
                phoneNumber = phoneNumber.replaceAll("\\s+", "").replaceAll("\\+", "");
                if (phoneNumber.startsWith("84")) {
                    phoneNumber = "0" + phoneNumber.substring(2);
                }

                User user = userService.findByPhoneNumber(phoneNumber);
                final String token = jwtTokenManager.generateToken(user);
                Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(604800);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                response.addCookie(cookie);
                log.info("{} has successfully logged in!", user.getUsername());

                return new LoginResponse(token, user.getUserType(), user.getUsername(),user.getId());
            }
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
        return null;
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
        response.addCookie(cookie);

        log.info("User has successfully logged out!");

        return new LogoutResponse("You have logged out successfully.");
    }
}
