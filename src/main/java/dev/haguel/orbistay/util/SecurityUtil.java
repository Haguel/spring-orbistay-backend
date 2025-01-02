package dev.haguel.orbistay.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtil {
    public static String getTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            log.info("Token extracted from Authorization header");
            return token;
        }

        log.warn("Authorization header is not valid");
        return null;
    }
}
