package com.fpt.sep490.security.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import java.util.Date;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenManager {
    private final JwtProperties jwtProperties;
    public String generateToken(User user) {
        final String username = user.getUsername();
        final UserType userType = user.getUserType();
        log.info("Generating token for user: {}, role: {}", username, userType);
        String token = JWT.create()
                .withSubject(username)
                .withIssuer(jwtProperties.getIssuer())
                .withClaim("role", userType.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMinute() * 60 * 1000))
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()));
        log.info("Generated token: {}", token);
        return token;
    }

    public String getUsernameFromToken(String token) {
        final DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    public static FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public boolean validateToken(String token, String authenticatedUserName) {
        final String usernameFromToken = getUsernameFromToken(token);
        final boolean equalsUsername = usernameFromToken.equals(authenticatedUserName);
        boolean tokenExpired = isTokenExpired(token);
        return equalsUsername && !tokenExpired;
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDateFromToken = getExpirationDateFromToken(token);
        return expirationDateFromToken.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        final DecodedJWT jwt = getDecodedJWT(token);
        return jwt.getExpiresAt();
    }

    private DecodedJWT getDecodedJWT(String token) {
        final JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes())).build();
        return verifier.verify(token);
    }
}
