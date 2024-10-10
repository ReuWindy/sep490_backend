package com.fpt.sep490.security.jwt;

import com.fpt.sep490.model.User;
import com.fpt.sep490.security.dto.AuthenticatedUserDto;
import com.fpt.sep490.security.dto.LoginRequest;
import com.fpt.sep490.security.dto.LoginResponse;
import com.fpt.sep490.security.mapper.UserMapper;
import com.fpt.sep490.security.service.UserService;
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

    public LoginResponse getLoginResponse(LoginRequest loginRequest) {

        final String username = loginRequest.getUsername();
        final String password = loginRequest.getPassword();

        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final AuthenticatedUserDto authenticatedUserDto = userService.findAuthenticatedUserByUsername(username);

        final User user = UserMapper.INSTANCE.convertToUser(authenticatedUserDto);
        final String token = jwtTokenManager.generateToken(user);

        log.info("{} has successfully logged in!", user.getUsername());

        return new LoginResponse(token, user.getUserType(), user.getUsername());
    }
}
