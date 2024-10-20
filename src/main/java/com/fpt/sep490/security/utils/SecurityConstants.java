package com.fpt.sep490.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityConstants {

    // FIXME : Customize security constants for your application.

    /**
     * Token expiration time 1 days.
     */
    public static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Secret key for signature
     */
    public static final String SECRET_KEY = "secret";

    /**
     * The company who provided token.
     * You can customize issuer name, this is given as an example.
     */
    public static final String ISSUER = "facebook.com/reuwindy";

    /**
     * Token Prefix
     * We will use this prefix when parsing JWT Token
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Authorization Prefix in HttpServletRequest
     * Authorization: <type> <credentials>
     * For Example : Authorization: Bearer YWxhZGxa1qea32GVuc2VzYW1l
     */
    public static final String HEADER_STRING = "Authorization";

    public static final String LOGIN_REQUEST_URI = "/login";

    public static final String REGISTRATION_REQUEST_URI = "/register";

    private SecurityConstants() {

        throw new UnsupportedOperationException();
    }

    /**
     * @return authenticated username from Security Context
     */
    public static String getAuthenticatedUsername() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }

}
