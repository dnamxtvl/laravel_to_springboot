package com.example.hello_sring_boot.utils;

public class JwtConstants {
    public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * Secret key for signature
     */
    public static final String SECRET_KEY = "mySecretKey";

    /**
     * The company who provided token.
     * You can customize issuer name, this is given as an example.
     */
    public static final String ISSUER = "www.boilerplate.design";

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

    private void SecurityConstants() {

        throw new UnsupportedOperationException();
    }
}
