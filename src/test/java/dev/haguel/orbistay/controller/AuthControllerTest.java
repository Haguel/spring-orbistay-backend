package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.RedisService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.TestDataGenerator;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
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

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenNewUserSignUp_thenSaveUserToDB_and_returnValidJwtTokens() {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUpRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        AppUser appUser = appUserRepository.findAppUserByEmail(signUpRequestDTO.getEmail()).orElse(null);

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

        webTestClient.post()
                .uri("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUpRequestDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenExistedUserSignInWithCorrectData_thenReturnValidJwtTokens() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);

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

        webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isNotFound();

        signInRequestDTO = SignInRequestDTO.builder()
                .email(correctEmail)
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenNewUserSignIn_thenReturnError() {
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUserLogout_thenRemoveEmailFromRedis() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertTrue(redisService.hasKey(email));
        assertTrue(jwtService.validateAccessToken(jwtResponseDTO.getAccessToken()));
        assertTrue(jwtService.validateRefreshToken(jwtResponseDTO.getRefreshToken()));

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(jwtResponseDTO.getRefreshToken())
                .build();

        webTestClient.post()
                .uri("/auth/log-out")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isOk();

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

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        String accessToken = jwtResponseDTO.getAccessToken();
        String newPassword;
        do {
            newPassword = TestDataGenerator.generateRandomPassword();
        } while (newPassword.equals(password));

        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .oldPassword(password)
                .newPassword(newPassword)
                .build();

        webTestClient.post()
                .uri("/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(changePasswordRequestDTO)
                .exchange()
                .expectStatus().isOk();

        signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(newPassword)
                .build();

        webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenUserChangePasswordWithIncorrectData_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        String accessToken = jwtResponseDTO.getAccessToken();
        String incorrectPassword;
        do {
            incorrectPassword = TestDataGenerator.generateRandomPassword();
        } while (incorrectPassword.equals(password));
        String newPassword;
        do {
            newPassword = TestDataGenerator.generateRandomPassword();
        } while (newPassword.equals(password));

        ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                .oldPassword(incorrectPassword)
                .newPassword(newPassword)
                .build();

        webTestClient.post()
                .uri("/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(changePasswordRequestDTO)
                .exchange()
                .expectStatus().isBadRequest();

        signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(newPassword)
                .build();

        webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUserRefreshTokensWithValidRefreshToken_thenReturnNewTokens() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        String refreshToken = jwtResponseDTO.getRefreshToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        JwtResponseDTO newJwtResponseDTO = webTestClient.post()
                .uri("/auth/refresh-tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotEquals(refreshToken, newJwtResponseDTO.getRefreshToken());
        assertTrue(jwtService.validateAccessToken(newJwtResponseDTO.getAccessToken()));
        assertTrue(jwtService.validateRefreshToken(newJwtResponseDTO.getRefreshToken()));
        assertEquals(redisService.getValue(email), newJwtResponseDTO.getRefreshToken());
    }

    @Test
    void whenUserRefreshTokensWithInvalidRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(TestDataGenerator.generateRandomJwtToken())
                .build();

        webTestClient.post()
                .uri("/auth/refresh-tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUserRefreshTokensWithUnbindRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        redisService.deleteValue(email);

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(jwtResponseDTO.getRefreshToken())
                .build();

        webTestClient.post()
                .uri("/auth/refresh-tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUserRefreshAccessTokenWithValidRefreshToken_thenReturnNewAccessToken() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(jwtResponseDTO.getRefreshToken())
                .build();

        JwtResponseDTO newJwtResponseDTO = webTestClient.post()
                .uri("/auth/refresh-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotEquals(jwtResponseDTO.getAccessToken(), newJwtResponseDTO.getAccessToken());
        assertTrue(jwtService.validateAccessToken(newJwtResponseDTO.getAccessToken()));
    }

    @Test
    void whenUserRefreshAccessTokenWithInvalidRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(TestDataGenerator.generateRandomJwtToken())
                .build();

        webTestClient.post()
                .uri("/auth/refresh-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUserRefreshAccessTokenWithUnbindRefreshToken_thenReturnError() {
        String email = "john.doe@example.com";
        String password = "password123";
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        JwtResponseDTO jwtResponseDTO = webTestClient.post()
                .uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();

        redisService.deleteValue(email);

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(jwtResponseDTO.getRefreshToken())
                .build();

        webTestClient.post()
                .uri("/auth/refresh-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jwtRefreshTokenDTO)
                .exchange()
                .expectStatus().isNotFound();
    }
}