package com.fpt.sep490.security.service;

import com.fpt.sep490.model.UserType;
import com.fpt.sep490.security.dto.AuthenticatedUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private static final String USERNAME_OR_PASSWORD_INVALID = "Invalid username or password.";
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AuthenticatedUserDto authenticatedUser = userService.findAuthenticatedUserByUsername(username);
        if (Objects.isNull(authenticatedUser)) {
            throw new UsernameNotFoundException(USERNAME_OR_PASSWORD_INVALID);
        }
        final String authenticatedUsername = authenticatedUser.getUsername();
        final String authenticatedPassword = authenticatedUser.getPassword();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if(authenticatedUser.getUserType().equals(UserType.ROLE_EMPLOYEE)) {
            final String role = userService.findEmployeeByUsername(authenticatedUsername).getRole().getEmployeeRole().getRoleName();
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_"+ role));
            return new User(authenticatedUsername, authenticatedPassword, authorities);
        }else{
            final UserType userType = authenticatedUser.getUserType();
            final SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userType.name());
            return new User(authenticatedUsername, authenticatedPassword, Collections.singletonList(grantedAuthority));
        }
    }
}