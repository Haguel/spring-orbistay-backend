package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.TestDataGenerator;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtService jwtService;

    @LocalServerPort
    int port;

    private final RestTemplate restTemplate;

    public AuthControllerTest() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    void whenNewUserSignUp_thenSaveUserToDB_and_returnValidJwtTokens() {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-up", HttpMethod.POST,
                new HttpEntity<>(signUpRequestDTO), JwtResponseDTO.class);
        AppUser appUser = appUserRepository.findAppUserByEmail(signUpRequestDTO.getEmail()).orElse(null);
        JwtResponseDTO jwtResponseDTO = response.getBody();

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(appUser);
        assertTrue(jwtService.validateAccessToken(jwtResponseDTO.getAccessToken()));
        assertTrue(jwtService.validateRefreshToken(jwtResponseDTO.getRefreshToken()));
        assertEquals(jwtService.getAccessClaims(jwtResponseDTO.getAccessToken()).getSubject(), appUser.getEmail());
        assertEquals(jwtService.getRefreshClaims(jwtResponseDTO.getRefreshToken()).getSubject(), appUser.getEmail());
    }

    @Test
    void whenExistedUserSignUp_thenReturnError() {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/sign-up", HttpMethod.POST,
                    new HttpEntity<>(signUpRequestDTO), JwtResponseDTO.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }
    }

    @Test
    void whenExistedUserSignInWithCorrectData_thenReturnValidJwtTokens() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);;
        AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);
        JwtResponseDTO jwtResponseDTO = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(jwtService.validateAccessToken(jwtResponseDTO.getAccessToken()));
        assertTrue(jwtService.validateRefreshToken(jwtResponseDTO.getRefreshToken()));
        assertEquals(jwtService.getAccessClaims(jwtResponseDTO.getAccessToken()).getSubject(), appUser.getEmail());
        assertEquals(jwtService.getRefreshClaims(jwtResponseDTO.getRefreshToken()).getSubject(), appUser.getEmail());
    }

    @Test
    void whenExistedUserSignInWithIncorrectData_thenReturnError() {
        String correctEmail = "john.doe@example.com";
        String correctPassword = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(TestDataGenerator.generateRandomEmail())
                .password(correctPassword)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                    new HttpEntity<>(signInRequestDTO), ErrorResponse.class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        signInRequestDTO = SignInRequestDTO.builder()
                .email(correctEmail)
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                    new HttpEntity<>(signInRequestDTO), ErrorResponse.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }
    }

    @Test
    void whenNewUserSignIn_thenReturnError() {
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                    new HttpEntity<>(signInRequestDTO), ErrorResponse.class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Test
    void whenUserLogout_thenRemoveEmailFromRedis() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        JwtResponseDTO jwtResponseDTO = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(redisService.hasKey(email));
        assertTrue(jwtService.validateAccessToken(jwtResponseDTO.getAccessToken()));
        assertTrue(jwtService.validateRefreshToken(jwtResponseDTO.getRefreshToken()));

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(jwtResponseDTO.getRefreshToken())
                .build();
        restTemplate.exchange(
                "http://localhost:" + port + "/auth/log-out", HttpMethod.POST,
                new HttpEntity<>(jwtRefreshTokenDTO), Void.class);
        assertFalse(redisService.hasKey(email));
    }

    @Test
    @Rollback
    void whenUserChangePasswordWithCorrectData_thenChangePassword() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        String accessToken = response.getBody().getAccessToken();
        String newPassword;
        do {
            newPassword = TestDataGenerator.generateRandomPassword();
        } while (newPassword.equals(password));
        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .accessToken(accessToken)
                .oldPassword(password)
                .newPassword(newPassword)
                .build();

        response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/change-password", HttpMethod.POST,
                new HttpEntity<>(changePasswordRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(newPassword)
                .build();

        response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        // revert changes
        changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .accessToken(accessToken)
                .oldPassword(newPassword)
                .newPassword(password)
                .build();

        response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/change-password", HttpMethod.POST,
                new HttpEntity<>(changePasswordRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void whenUserChangePasswordWithIncorrectData_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        String accessToken = response.getBody().getAccessToken();
        String incorrectPassword;
        do {
            incorrectPassword = TestDataGenerator.generateRandomPassword();
        } while (incorrectPassword.equals(password));
        String newPassword;
        do {
            newPassword = TestDataGenerator.generateRandomPassword();
        } while (newPassword.equals(password));
        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .accessToken(accessToken)
                .oldPassword(incorrectPassword)
                .newPassword(newPassword)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/change-password", HttpMethod.POST,
                    new HttpEntity<>(changePasswordRequestDTO), ErrorResponse.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }

        signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(newPassword)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                    new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }
    }

    @Test
    void whenUserRefreshTokensWithValidRefreshToken_thenReturnNewTokens() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        String refreshToken = response.getBody().getRefreshToken();
        String accessToken = response.getBody().getAccessToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/refresh-tokens", HttpMethod.POST,
                new HttpEntity<>(jwtRefreshTokenDTO), JwtResponseDTO.class);;
        assertEquals(200, response.getStatusCode().value());

        String newRefreshToken = response.getBody().getRefreshToken();
        String newAccessToken = response.getBody().getAccessToken();

        assertNotEquals(refreshToken, newRefreshToken);
        assertNotEquals(accessToken, newAccessToken);
        assertTrue(jwtService.validateAccessToken(newAccessToken));
        assertTrue(jwtService.validateRefreshToken(newRefreshToken));
        assertEquals(redisService.getValue(email), newRefreshToken);
    }

    @Test
    void whenUserRefreshTokensWithInvalidRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(TestDataGenerator.generateRandomJwtToken())
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/refresh-tokens", HttpMethod.POST,
                    new HttpEntity<>(jwtRefreshTokenDTO), ErrorResponse.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }
    }

    @Test
    void whenUserRefreshTokensWithUnbindRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        redisService.deleteValue(email);

        String refreshToken = response.getBody().getRefreshToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/refresh-tokens", HttpMethod.POST,
                    new HttpEntity<>(jwtRefreshTokenDTO), ErrorResponse.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }
    }

    @Test
    void whenUserRefreshAccessTokenWithValidRefreshToken_thenReturnNewAccessToken() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        String refreshToken = response.getBody().getRefreshToken();
        String accessToken = response.getBody().getAccessToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/refresh-access-token", HttpMethod.POST,
                new HttpEntity<>(jwtRefreshTokenDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        String newAccessToken = response.getBody().getAccessToken();

        assertNotEquals(accessToken, newAccessToken);
        assertTrue(jwtService.validateAccessToken(newAccessToken));
    }

    @Test
    void whenUserRefreshAccessTokenWithInvalidRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(TestDataGenerator.generateRandomJwtToken())
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/refresh-access-token", HttpMethod.POST,
                    new HttpEntity<>(jwtRefreshTokenDTO), ErrorResponse.class);
            fail("Should have thrown 400 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400"));
        }
    }

    @Test
    void whenUserRefreshAccessTokenWithUnbindRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        redisService.deleteValue(email);

        String refreshToken = response.getBody().getRefreshToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/auth/refresh-access-token", HttpMethod.POST,
                    new HttpEntity<>(jwtRefreshTokenDTO), ErrorResponse.class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }
}