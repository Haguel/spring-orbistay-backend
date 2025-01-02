package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtService(
            @Value("${jwt.access.secret}") String jwtAccessSecret,
            @Value("${jwt.refresh.secret}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(UserDetails userDetails) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessIssuedInstant = now.atZone(ZoneId.systemDefault()).toInstant();
        final Instant accessExpirationInstant = now.plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant();

        if(userDetails instanceof AppUser appUser) {
            String token = Jwts.builder()
                    .setSubject(appUser.getEmail())
                    .setExpiration(Date.from(accessExpirationInstant))
                    .setIssuedAt(Date.from(accessIssuedInstant))
                    .signWith(jwtAccessSecret)
                    .claim("role", appUser.getRole())
                    .claim("username", appUser.getUsername())
                    .claim("uuid", UUID.randomUUID().toString())
                    .compact();

            log.info("JWT access token generated");

            return token;
        }

        log.warn("JWT access token generation failed because provided UserDetails is not an instance of AppUser");

        return null;
    }

    public String generateRefreshToken(UserDetails userDetails) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshIssuedInstant = now.atZone(ZoneId.systemDefault()).toInstant();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();

        if(userDetails instanceof AppUser appUser) {
            String token = Jwts.builder()
                    .setSubject(appUser.getEmail())
                    .setIssuedAt(Date.from(refreshIssuedInstant))
                    .setExpiration(Date.from(refreshExpirationInstant))
                    .signWith(jwtRefreshSecret)
                    .claim("uuid", UUID.randomUUID().toString())
                    .compact();

            log.info("JWT refresh token generated");

            return token;
        }

        log.warn("JWT refresh token generation failed because provided UserDetails is not an instance of AppUser");

        return null;
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}