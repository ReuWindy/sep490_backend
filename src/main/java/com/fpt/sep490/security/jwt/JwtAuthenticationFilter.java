package com.fpt.sep490.security.jwt;

import com.fpt.sep490.security.service.UserDetailServiceImpl;
import com.fpt.sep490.security.utils.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;
    private final UserDetailServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final String requestURI = req.getRequestURI();

        if (requestURI.contains(SecurityConstants.LOGIN_REQUEST_URI) || requestURI.contains(SecurityConstants.REGISTRATION_REQUEST_URI)) {
            chain.doFilter(req, res);
            return;
        }

        String username = null;
        String authToken = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for(Cookie cookie : cookies) {
                if("token".equals(cookie.getName())) {
                    authToken = cookie.getValue();
                    try {
                        username = jwtTokenManager.getUsernameFromToken(authToken);
                    } catch (Exception e) {
                        log.error("Authentication Exception : {}", e.getMessage());
                    }
                    break;
                }
            }
        }
        log.info("Extracted Token: {}", authToken);

        final SecurityContext securityContext = SecurityContextHolder.getContext();

        if (Objects.nonNull(username) && Objects.isNull(securityContext.getAuthentication())) {

            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenManager.validateToken(authToken, userDetails.getUsername())) {
                log.info("Token is valid for username: {}", username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                log.info("Authenticated user: {} ", username);
                securityContext.setAuthentication(authentication);
            } else {
                log.warn("Invalid token for username: {}", username);
            }
        }
        chain.doFilter(req, res);
        log.info("Request processing completed for: {}", requestURI);
    }

}
