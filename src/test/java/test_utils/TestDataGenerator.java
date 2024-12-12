package test_utils;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.enumeration.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SECRET_KEY = "VFJ8UVtEPj4f0OL9HVG58XIcrEkd8q9PUpLIkxmiqno=";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 12;
    private static final String[] EMAIL_DOMAINS = {"example.com", "test.com", "sample.org"};

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        Random random = new Random();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }

    public static String convertPasswordToHash(String password) {
        return passwordEncoder.encode(password);
    }

    public static String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandomEmail() {
        String username = generateRandomUsername();
        String domain = EMAIL_DOMAINS[new Random().nextInt(EMAIL_DOMAINS.length)];
        return username + "@" + domain;
    }

    public static Role generateRandomRole() {
        return Role.values()[new Random().nextInt(Role.values().length)];
    }

    public static AppUser generateRandomUser() {
        return AppUser.builder()
                .email(generateRandomEmail())
                .username(generateRandomUsername())
                .passwordHash(convertPasswordToHash(generateRandomPassword()))
                .role(generateRandomRole())
                .build();
    }

    public static String generateRandomJwtToken() {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims generateClaims(String subject) {
        return Jwts.claims()
                .setSubject(subject);
    }
}