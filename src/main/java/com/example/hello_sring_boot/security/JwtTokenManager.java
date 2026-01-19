package com.example.hello_sring_boot.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.enums.UserType;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenManager {
    private final JwtProperties jwtProperties;

    public JwtTokenManager(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public LoginResponse generateToken(User user) {
        final String email = user.getEmail();
        final String userId = user.getId();
        final UserType userRole = UserType.fromValue(user.getTypeUser());

        //@formatter:off
        String accessToken = JWT.create()
                .withSubject(userId)
                .withClaim("email", email)
                .withIssuer(jwtProperties.getIssuer())
                .withClaim("role", userRole.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMinute() * 60 * 1000))
                .withJWTId(UUID.randomUUID() + new Date(System.currentTimeMillis()).toString())
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()));

        String refreshToken = JWT.create()
                .withSubject(userId)
                .withClaim("email", email)
                .withIssuer(jwtProperties.getIssuer())
                .withClaim("role", userRole.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMinuteRefresh() * 60 * 1000))
                .withJWTId(UUID.randomUUID() + new Date(System.currentTimeMillis()).toString())
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKeyRefresh().getBytes()));

        return new LoginResponse(accessToken, refreshToken);
        //@formatter:on
    }

    public String getUserIdFromAccessToken(String token) {
        final DecodedJWT decodedJWT = getDecodedJWT(token, jwtProperties.getSecretKey());

        return decodedJWT.getSubject();
    }

    public String getUserIdFromRefreshToken(String token) {
        final DecodedJWT decodedJWT = getDecodedJWT(token, jwtProperties.getSecretKeyRefresh());

        return decodedJWT.getSubject();
    }

    public boolean validateAccessToken(String token, String authenticatedUsername) {
        final String userIdFromToken = getUserIdFromAccessToken(token);
        final boolean equalsUserId = userIdFromToken.equals(authenticatedUsername);
        final boolean tokenExpired = isTokenExpired(token, jwtProperties.getSecretKey());

        return equalsUserId && !tokenExpired;
    }

    public boolean validateRefreshToken(String token, String authenticatedUsername) {
        final String userIdFromToken = getUserIdFromRefreshToken(token);
        final boolean equalsUserId = userIdFromToken.equals(authenticatedUsername);
        final boolean tokenExpired = isTokenExpired(token, jwtProperties.getSecretKeyRefresh());

        return equalsUserId && !tokenExpired;
    }

    private boolean isTokenExpired(String token, String secret) {
        final Date expirationDateFromToken = getExpirationDateFromToken(token, secret);
        return expirationDateFromToken.before(new Date());
    }

    private Date getExpirationDateFromToken(String token, String secret) {

        final DecodedJWT decodedJWT = getDecodedJWT(token, secret);

        return decodedJWT.getExpiresAt();
    }

    private DecodedJWT getDecodedJWT(String token, String secret) {

        final JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret.getBytes())).build();

        return jwtVerifier.verify(token);
    }
}
