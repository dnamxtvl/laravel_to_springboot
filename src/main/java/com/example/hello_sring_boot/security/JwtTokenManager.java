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
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()));

        String refreshToken = JWT.create()
                .withSubject(userId)
                .withClaim("email", email)
                .withIssuer(jwtProperties.getIssuer())
                .withClaim("role", userRole.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMinuteRefresh() * 60 * 1000))
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKeyRefresh().getBytes()));

        return new LoginResponse(accessToken, refreshToken);
        //@formatter:on
    }

    public String getEmailFromToken(String token) {

        final DecodedJWT decodedJWT = getDecodedJWT(token);

        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token, String authenticatedUsername) {

        final String usernameFromToken = getEmailFromToken(token);

        final boolean equalsUsername = usernameFromToken.equals(authenticatedUsername);
        final boolean tokenExpired = isTokenExpired(token);

        return equalsUsername && !tokenExpired;
    }

    private boolean isTokenExpired(String token) {

        final Date expirationDateFromToken = getExpirationDateFromToken(token);
        return expirationDateFromToken.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {

        final DecodedJWT decodedJWT = getDecodedJWT(token);

        return decodedJWT.getExpiresAt();
    }

    private DecodedJWT getDecodedJWT(String token) {

        final JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes())).build();

        return jwtVerifier.verify(token);
    }
}
