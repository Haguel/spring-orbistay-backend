package test_utils;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.enumeration.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SECRET_KEY = "VFJ8UVtEPj4f0OL9HVG58XIcrEkd8q9PUpLIkxmiqno=";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 12;
    private static final String[] firstNames = {"John", "Jane", "Alex", "Emily", "Chris", "Katie", "Michael", "Sarah", "David", "Laura"};
    public static final String[] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
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

    public static String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }

        return phoneNumber.toString();
    }

    public static String generateRandomStringDate() {
        long minDay = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2100, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return randomDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String generateRandomGender() {
        return new Random().nextBoolean() ? "MALE" : "FEMALE";
    }

    public static String generateRandomCountryId() {
        return String.valueOf(new Random().nextInt(194));
    }

    public static String generateRandomCity() {
        return "City_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandomStreet() {
        return "Street_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandomPassportNumber() {
        return "AB" + new Random().nextInt(1000000);
    }

    public static String generateRandomFirstName() {
        return firstNames[new Random().nextInt(firstNames.length)];
    }

    public static String generateRandomLastName() {
        return lastNames[new Random().nextInt(lastNames.length)];
    }

    public static String generateRandomExpirationDate(boolean isExpired) {
        LocalDate date = LocalDate.now().plusYears(new Random().nextInt(10));
        if (isExpired) {
            date = date.minusYears(10);
        }

        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
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