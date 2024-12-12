package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
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
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void whenNewUserSignUp_thenSaveUserToDB_and_returnValidJwtTokens() {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
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
                .username(TestDataGenerator.generateRandomUsername())
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());
    }

    @Test
    void whenExistedUserSignInWithCorrectData_thenReturnValidJwtTokens() {
        String username = TestDataGenerator.generateRandomUsername();
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        response = testRestTemplate.postForEntity("/auth/sign-in", signInRequestDTO, JwtResponseDTO.class);
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
        String username = TestDataGenerator.generateRandomUsername();
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        String incorrectPassword;
        do {
            incorrectPassword = TestDataGenerator.generateRandomPassword();
        } while (incorrectPassword.equals(password));
        String incorrectEmail;
        do {
            incorrectEmail = TestDataGenerator.generateRandomEmail();
        } while (incorrectEmail.equals(email));

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());


        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(incorrectPassword)
                .build();
        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/sign-in", signInRequestDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());

        signInRequestDTO = SignInRequestDTO.builder()
                .email(incorrectEmail)
                .password(password)
                .build();
        errorResponse = testRestTemplate.postForEntity("/auth/sign-in", signInRequestDTO, ErrorResponse.class);
        assertEquals(404, errorResponse.getStatusCode().value());
    }

    @Test
    void whenNewUserSignIn_thenReturnError() {
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(TestDataGenerator.generateRandomEmail())
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/sign-in", signInRequestDTO, ErrorResponse.class);
        assertEquals(404, errorResponse.getStatusCode().value());
    }

    @Test
    void whenUserLogout_thenRemoveEmailFromRedis() {
        String email = TestDataGenerator.generateRandomEmail();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(TestDataGenerator.generateRandomPassword())
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        JwtResponseDTO jwtResponseDTO = response.getBody();

        assertEquals(201, response.getStatusCode().value());
        assertTrue(redisService.hasKey(email));
        assertTrue(jwtService.validateAccessToken(jwtResponseDTO.getAccessToken()));
        assertTrue(jwtService.validateRefreshToken(jwtResponseDTO.getRefreshToken()));

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(jwtResponseDTO.getRefreshToken())
                .build();
        testRestTemplate.postForEntity("/auth/log-out", jwtRefreshTokenDTO, Void.class);
        assertFalse(redisService.hasKey(email));
    }

    @Test
    void whenUserChangePasswordWithCorrectData_thenChangePassword() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

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

        response = testRestTemplate.postForEntity("/auth/change-password", changePasswordRequestDTO, JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());


        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(newPassword)
                .build();

        response = testRestTemplate.postForEntity("/auth/sign-in", signInRequestDTO, JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void whenUserChangePasswordWithIncorrectData_thenReturnError() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

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

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/change-password", changePasswordRequestDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());


        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(newPassword)
                .build();

        errorResponse = testRestTemplate.postForEntity("/auth/sign-in", signInRequestDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());
    }

    @Test
    void whenUserRefreshTokensWithValidRefreshToken_thenReturnNewTokens() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        String refreshToken = response.getBody().getRefreshToken();
        String accessToken = response.getBody().getAccessToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        response = testRestTemplate.postForEntity("/auth/refresh-tokens", jwtRefreshTokenDTO, JwtResponseDTO.class);
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
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(TestDataGenerator.generateRandomJwtToken())
                .build();

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/refresh-tokens", jwtRefreshTokenDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());
    }

    @Test
    void whenUserRefreshTokensWithUnbindRefreshToken_thenReturnError() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        redisService.deleteValue(email);

        String refreshToken = response.getBody().getRefreshToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/refresh-tokens", jwtRefreshTokenDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());
    }

    @Test
    void whenUserRefreshAccessTokenWithValidRefreshToken_thenReturnNewAccessToken() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        String refreshToken = response.getBody().getRefreshToken();
        String accessToken = response.getBody().getAccessToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        response = testRestTemplate.postForEntity("/auth/refresh-access-token", jwtRefreshTokenDTO, JwtResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());

        String newAccessToken = response.getBody().getAccessToken();

        assertNotEquals(accessToken, newAccessToken);
        assertTrue(jwtService.validateAccessToken(newAccessToken));
    }

    @Test
    void whenUserRefreshAccessTokenWithInvalidRefreshToken_thenReturnError() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(TestDataGenerator.generateRandomJwtToken())
                .build();

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/refresh-access-token", jwtRefreshTokenDTO, ErrorResponse.class);
        assertEquals(400, errorResponse.getStatusCode().value());
    }

    @Test
    void whenUserRefreshAccessTokenWithUnbindRefreshToken_thenReturnError() {
        String email = TestDataGenerator.generateRandomEmail();
        String password = TestDataGenerator.generateRandomPassword();
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(TestDataGenerator.generateRandomUsername())
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = testRestTemplate.postForEntity("/auth/sign-up", signUpRequestDTO, JwtResponseDTO.class);
        assertEquals(201, response.getStatusCode().value());

        redisService.deleteValue(email);

        String refreshToken = response.getBody().getRefreshToken();
        JwtRefreshTokenDTO jwtRefreshTokenDTO = JwtRefreshTokenDTO.builder()
                .refreshToken(refreshToken)
                .build();

        ResponseEntity<ErrorResponse> errorResponse = testRestTemplate.postForEntity("/auth/refresh-access-token", jwtRefreshTokenDTO, ErrorResponse.class);
        assertEquals(404, errorResponse.getStatusCode().value());
    }
}