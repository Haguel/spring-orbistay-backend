package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.ChangePasswordRequestDTO;
import dev.haguel.orbistay.dto.request.JwtRefreshTokenRequestDTO;
import dev.haguel.orbistay.dto.request.SignInRequestDTO;
import dev.haguel.orbistay.dto.request.SignUpRequestDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.RedisService;
import dev.haguel.orbistay.util.EndPoints;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.TestDataGenerator;
import test_utils.TestDataStorage;

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

    @Nested
    class SignUp {
        @Test
        void whenNewUserSignUp_thenSaveUserToDB_and_returnValidJwtTokens() {
            SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                    .username(TestDataGenerator.generateRandomUsername())
                    .email(TestDataGenerator.generateRandomEmail())
                    .password(TestDataGenerator.generateRandomPassword())
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_UP)
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
                    .email(TestDataStorage.JOHN_DOE_EMAIL)
                    .password(TestDataStorage.JOHN_DOE_PASSWORD)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_UP)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signUpRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class SignIn {
        @Test
        void whenExistedUserSignInWithCorrectData_thenReturnValidJwtTokens() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
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
            String correctEmail = TestDataStorage.JOHN_DOE_EMAIL;
            String correctPassword = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(TestDataGenerator.generateRandomEmail())
                    .password(correctPassword)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isNotFound();

            signInRequestDTO = SignInRequestDTO.builder()
                    .email(correctEmail)
                    .password(TestDataGenerator.generateRandomPassword())
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
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
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class LogOut {
        @Test
        void whenUserLogout_thenRemoveEmailFromRedis() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
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

            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(jwtResponseDTO.getRefreshToken())
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.LOG_OUT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
                    .exchange()
                    .expectStatus().isOk();

            assertFalse(redisService.hasKey(email));
        }
    }

    @Nested
    class ChangePassword {
        @Test
        void whenUserChangePasswordWithCorrectData_thenChangePassword() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
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
                    .uri(EndPoints.Auth.CHANGE_PASSWORD)
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
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenUserChangePasswordWithIncorrectData_thenReturnError() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
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
                    .uri(EndPoints.Auth.CHANGE_PASSWORD)
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
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class RefreshTokens {
        @Test
        void whenUserRefreshTokensWithValidRefreshToken_thenReturnNewTokens() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(JwtResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            String refreshToken = jwtResponseDTO.getRefreshToken();
            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(refreshToken)
                    .build();

            JwtResponseDTO newJwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_TOKENS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
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
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(JwtResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(TestDataGenerator.generateRandomJwtToken())
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_TOKENS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void whenUserRefreshTokensWithUnbindRefreshToken_thenReturnError() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(JwtResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            redisService.deleteValue(email);

            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(jwtResponseDTO.getRefreshToken())
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_TOKENS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class RefreshAccessToken {
        @Test
        void whenUserRefreshAccessTokenWithValidRefreshToken_thenReturnNewAccessToken() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(JwtResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(jwtResponseDTO.getRefreshToken())
                    .build();

            JwtResponseDTO newJwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
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
            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(TestDataGenerator.generateRandomJwtToken())
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void whenUserRefreshAccessTokenWithUnbindRefreshToken_thenReturnError() {
            String email = TestDataStorage.JOHN_DOE_EMAIL;
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .build();

            JwtResponseDTO jwtResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(JwtResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            redisService.deleteValue(email);

            JwtRefreshTokenRequestDTO jwtRefreshTokenRequestDTO = JwtRefreshTokenRequestDTO.builder()
                    .refreshToken(jwtResponseDTO.getRefreshToken())
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jwtRefreshTokenRequestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}