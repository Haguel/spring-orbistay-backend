package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.*;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.RedisService;
import dev.haguel.orbistay.util.EndPoints;
import dev.haguel.orbistay.util.Generator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;
import test_utils.TestDataGenerator;
import test_utils.TestDataStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerIntegrationTest extends BaseControllerTestClass {
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

    @Nested
    class SignUp {
        @Test
        void whenNewUserSignUp_thenSaveUserToDB_and_returnValidJwtTokens() {
            SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                    .username(TestDataGenerator.generateRandomUsername())
                    .email(TestDataGenerator.generateRandomEmail())
                    .password(TestDataGenerator.generateRandomPassword())
                    .build();

            AccessTokenResponseDTO accessTokenResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_UP)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signUpRequestDTO)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().exists(HttpHeaders.SET_COOKIE)
                    .expectHeader().valueMatches(HttpHeaders.SET_COOKIE, "refresh_token=.*; Path=/; Max-Age=\\d+; Expires=.*; HttpOnly; SameSite=Lax")
                    .expectBody(AccessTokenResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            AppUser appUser = appUserRepository.findAppUserByEmail(signUpRequestDTO.getEmail()).orElse(null);

            assertNotNull(appUser);
            assertFalse(appUser.getEmailVerification().isVerified());
            assertEquals(LocalDate.now().plusDays(1), appUser.getEmailVerification().getExpiresAt().toLocalDate());
            assertTrue(jwtService.validateAccessToken(accessTokenResponseDTO.getAccessToken()));
            assertEquals(jwtService.getAccessClaims(accessTokenResponseDTO.getAccessToken()).getSubject(), appUser.getEmail());
        }

        @Test
        void whenExistedUserSignUp_thenReturn400() {
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

            AccessTokenResponseDTO accessTokenResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.SIGN_IN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(signInRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().exists(HttpHeaders.SET_COOKIE)
                    .expectHeader().valueMatches(HttpHeaders.SET_COOKIE, "refresh_token=.*; Path=/; Max-Age=\\d+; Expires=.*; HttpOnly; SameSite=Lax")
                    .expectBody(AccessTokenResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);

            assertTrue(jwtService.validateAccessToken(accessTokenResponseDTO.getAccessToken()));
            assertEquals(jwtService.getAccessClaims(accessTokenResponseDTO.getAccessToken()).getSubject(), appUser.getEmail());
        }

        @Test
        void whenExistedUserSignInWithInvalidEmail_thenReturn404() {
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
        }

        @Test
        void whenExistedUserSignInWithInvalidPassword_thenReturn400() {
            String correctEmail = TestDataStorage.JOHN_DOE_EMAIL;

            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
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
        void whenNewUserSignIn_thenReturn404() {
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
            String refreshToken = SharedTestUtil.signInJohnDoeAndGetRefreshToken(webTestClient);

            assertTrue(redisService.hasAuthKey(TestDataStorage.JOHN_DOE_EMAIL));

            webTestClient.post()
                    .uri(EndPoints.Auth.LOG_OUT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie("refresh_token", refreshToken)
                    .exchange()
                    .expectStatus().isOk();

            assertFalse(redisService.hasAuthKey(TestDataStorage.JOHN_DOE_EMAIL));
        }
    }

    @Nested
    class ChangePassword {
        @Test
        void whenUserChangePasswordWithCorrectData_thenChangePassword() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            String accessToken = accessTokenResponseDTO.getAccessToken();
            String newPassword;
            do {
                newPassword = TestDataGenerator.generateRandomPassword();
            } while (newPassword.equals(TestDataStorage.JOHN_DOE_PASSWORD));

            ChangePasswordRequestDTO changePasswordRequestDTO = ChangePasswordRequestDTO.builder()
                    .oldPassword(TestDataStorage.JOHN_DOE_PASSWORD)
                    .newPassword(newPassword)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.CHANGE_PASSWORD)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(changePasswordRequestDTO)
                    .exchange()
                    .expectStatus().isOk();

            SharedTestUtil.signInAndGetAccessToken(TestDataStorage.JOHN_DOE_EMAIL, newPassword, webTestClient);
        }

        @Test
        void whenUserChangePasswordWithIncorrectData_thenReturn400() {
            String accessToken = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient).getAccessToken();
            String password = TestDataStorage.JOHN_DOE_PASSWORD;
            String email = TestDataStorage.JOHN_DOE_EMAIL;

            String incorrectPassword;
            do {
                incorrectPassword = TestDataGenerator.generateRandomPassword();
            } while (incorrectPassword.equals(password));
            String newPassword;
            do {
                newPassword = TestDataGenerator.generateRandomPassword();
            } while (newPassword.equals(password));

            // Change password with incorrect old password
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

            // Try to sign in with new password
            SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
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
    class RefreshAccessToken {
        @Test
        void whenUserRefreshAccessTokenWithValidRefreshToken_thenReturnNewAccessToken() {
            String refreshToken = SharedTestUtil.signInJohnDoeAndGetRefreshToken(webTestClient);

            AccessTokenResponseDTO newAccessTokenResponseDTO = webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie("refresh_token", refreshToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AccessTokenResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            assertNotNull(newAccessTokenResponseDTO);
            assertTrue(jwtService.validateAccessToken(newAccessTokenResponseDTO.getAccessToken()));
        }

        @Test
        void whenUserRefreshAccessTokenWithInvalidRefreshToken_thenReturn400() {
            webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie("refresh_token", Generator.generateRandomString(100))
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void whenUserRefreshAccessTokenWithUnbindRefreshToken_thenReturn404() {
            String refreshToken = SharedTestUtil.signInJohnDoeAndGetRefreshToken(webTestClient);

            redisService.deleteAuthValue(TestDataStorage.JOHN_DOE_EMAIL);

            webTestClient.post()
                    .uri(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie("refresh_token", refreshToken)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class VerifyEmail {
        @Test
        void whenUserVerifyEmailWithCorrectToken_thenVerifyEmail() {
            String username = TestDataGenerator.generateRandomUsername();
            String email = TestDataGenerator.generateRandomEmail();
            String password = TestDataGenerator.generateRandomPassword();

            SharedTestUtil.signUpAndGetAccessToken(username, email, password, webTestClient);

            AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);
            String token = appUser.getEmailVerification().getToken();

            webTestClient.post()
                    .uri(EndPoints.Auth.VERIFY_EMAIL + "?token=" + token)
                    .exchange()
                    .expectStatus().isOk();

            appUser = appUserRepository.findAppUserByEmail(appUser.getEmail()).orElse(null);

            assertTrue(appUser.getEmailVerification().isVerified());
            assertNull(appUser.getEmailVerification().getToken());
        }

        @Test
        void whenUserVerifyEmailWithIncorrectToken_thenReturn404() {
            String username = TestDataGenerator.generateRandomUsername();
            String email = TestDataGenerator.generateRandomEmail();
            String password = TestDataGenerator.generateRandomPassword();

            SharedTestUtil.signUpAndGetAccessToken(username, email, password, webTestClient);

            String token = Generator.generateRandomString(10);

            webTestClient.post()
                    .uri(EndPoints.Auth.VERIFY_EMAIL + "?token=" + token)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void whenUserVerifyEmailWithExpiredVerification_thenReturn401() {
            String token = "654321";

            webTestClient.post()
                    .uri(EndPoints.Auth.VERIFY_EMAIL + "?token=" + token)
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }

    @Nested
    class ResendVerificationEmail {
        @Test
        void whenUserResendVerificationEmail_thenSendNewEmail() {
            String username = TestDataGenerator.generateRandomUsername();
            String email = TestDataGenerator.generateRandomEmail();
            String password = TestDataGenerator.generateRandomPassword();

            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signUpAndGetAccessToken(username, email, password, webTestClient);

            AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);
            String oldToken = appUser.getEmailVerification().getToken();

            webTestClient.post()
                    .uri(EndPoints.Auth.RESEND_EMAIL_VERIFICATION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();

            appUser = appUserRepository.findAppUserByEmail(appUser.getEmail()).orElse(null);

            assertFalse(appUser.getEmailVerification().isVerified());
            assertEquals(LocalDate.now().plusDays(1), appUser.getEmailVerification().getExpiresAt().toLocalDate());
            assertNotEquals(oldToken, appUser.getEmailVerification().getToken());
        }

        @Test
        void whenVerifiedUserResendVerificationEmail_thenReturn400() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.post()
                    .uri(EndPoints.Auth.RESEND_EMAIL_VERIFICATION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class ResetPassword {
        @Test
        void whenUserRequestResetPassword_thenSendEmailWithResetTokenAndSaveToken() {
            String email = "john.doe@example.com";
            RequestPasswordResetRequestDTO requestPasswordResetRequestDTO = RequestPasswordResetRequestDTO.builder()
                    .email(email)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REQUEST_RESET_PASSWORD)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestPasswordResetRequestDTO)
                    .exchange()
                    .expectStatus().isOk();

            assertNotNull(redisService.getResetPasswordValue(email));
        }

        @Test
        void whenUserRequestResetPasswordWithIncorrectEmail_return404() {
            String email = "john_doe@example.com";
            RequestPasswordResetRequestDTO requestPasswordResetRequestDTO = RequestPasswordResetRequestDTO.builder()
                    .email(email)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REQUEST_RESET_PASSWORD)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestPasswordResetRequestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void whenUserResetPasswordWithCorrectToken_thenChangePassword() {
            String email = "john.doe@example.com";
            RequestPasswordResetRequestDTO requestPasswordResetRequestDTO = RequestPasswordResetRequestDTO.builder()
                    .email(email)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.REQUEST_RESET_PASSWORD)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestPasswordResetRequestDTO)
                    .exchange()
                    .expectStatus().isOk();

            String token = redisService.getResetPasswordValue(email);
            assertNotNull(token);

            String newPassword = TestDataGenerator.generateRandomPassword();
            PasswordResetRequestDTO resetPasswordRequestDTO = PasswordResetRequestDTO.builder()
                    .newPassword(newPassword)
                    .resetPasswordJwtToken(token)
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Auth.RESET_PASSWORD)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(resetPasswordRequestDTO)
                    .exchange()
                    .expectStatus().isOk();

            SharedTestUtil.signInAndGetAccessToken(email, newPassword, webTestClient);
        }
    }
}