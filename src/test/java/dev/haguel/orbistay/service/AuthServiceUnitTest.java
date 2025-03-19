package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.ChangePasswordRequestDTO;
import dev.haguel.orbistay.dto.request.SignInRequestDTO;
import dev.haguel.orbistay.dto.request.SignUpRequestDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.dto.response.JwtDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.EmailVerification;
import dev.haguel.orbistay.entity.enumeration.Role;
import dev.haguel.orbistay.event.AppUserSignUpEvent;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.factory.EmailMessageFactory;
import dev.haguel.orbistay.factory.MessageFactory;
import dev.haguel.orbistay.strategy.notification.context.NotificationContext;
import dev.haguel.orbistay.strategy.notification.context.NotificationType;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppUserService appUserService;

    @Mock
    private UserDetailsCustomService userDetailsCustomService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private NotificationContext notificationContext;

    @Mock
    private MessageFactory messageFactory;

    @InjectMocks
    private AuthService authService;

    @Nested
    class SignUp {
        @Test
        void whenSignUpWithValidData_thenReturnJwtDTO() {
            SignUpRequestDTO request = new SignUpRequestDTO("user", "user@example.com", "password");
            AppUser appUser = AppUser.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash("encodedPassword")
                    .role(Role.ROLE_USER)
                    .build();
            EmailVerification verification = new EmailVerification();
            String accessToken = "accessToken";
            String refreshToken = "refreshToken";

            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(appUserService.save(any(AppUser.class))).thenReturn(appUser);
            when(emailVerificationService.createNeededVerificationForAppUser(appUser)).thenReturn(verification);
            when(jwtService.generateAccessToken(appUser)).thenReturn(accessToken);
            when(jwtService.generateRefreshToken(appUser)).thenReturn(refreshToken);

            JwtDTO result = authService.signUp(request);

            assertNotNull(result);
            assertEquals(accessToken, result.getAccessToken());
            assertEquals(refreshToken, result.getRefreshToken());
            verify(appUserService, times(2)).save(any(AppUser.class));
            verify(emailVerificationService).createNeededVerificationForAppUser(appUser);
            verify(redisService).setAuthValue(appUser.getEmail(), refreshToken);
            verify(applicationEventPublisher).publishEvent(any(AppUserSignUpEvent.class));
            verify(notificationContext).setNotificationType(NotificationType.EMAIL);
        }

        @Test
        void whenSignUpWithDuplicateUsername_thenThrowUniquenessViolationException() {
            SignUpRequestDTO request = new SignUpRequestDTO("existingUser", "new@example.com", "password");
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(appUserService.save(any(AppUser.class)))
                    .thenThrow(new DataIntegrityViolationException("app_user_username_key"));

            UniquenessViolationException exception = assertThrows(UniquenessViolationException.class,
                    () -> authService.signUp(request));
            assertEquals("Username already exists", exception.getMessage());
        }

        @Test
        void whenSignUpWithDuplicateEmail_thenThrowUniquenessViolationException() {
            SignUpRequestDTO request = new SignUpRequestDTO("newUser", "existing@example.com", "password");
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(appUserService.save(any(AppUser.class)))
                    .thenThrow(new DataIntegrityViolationException("app_user_email_key"));

            UniquenessViolationException exception = assertThrows(UniquenessViolationException.class,
                    () -> authService.signUp(request));
            assertEquals("Email already exists", exception.getMessage());
        }
    }

    @Nested
    class SignIn {
        @Test
        void whenSignInWithCorrectCredentials_thenReturnJwtDTO() {
            SignInRequestDTO request = new SignInRequestDTO("user@example.com", "password");
            AppUser appUser = new AppUser();
            appUser.setEmail(request.getEmail());
            String accessToken = "accessToken";
            String refreshToken = "refreshToken";

            when(userDetailsCustomService.loadUserByUsername(request.getEmail())).thenReturn(appUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(null);
            when(jwtService.generateAccessToken(appUser)).thenReturn(accessToken);
            when(jwtService.generateRefreshToken(appUser)).thenReturn(refreshToken);

            JwtDTO result = authService.signIn(request);

            assertNotNull(result);
            assertEquals(accessToken, result.getAccessToken());
            assertEquals(refreshToken, result.getRefreshToken());
            verify(redisService).setAuthValue(appUser.getEmail(), refreshToken);
        }

        @Test
        void whenSignInWithNonExistentUser_thenThrowAppUserNotFoundException() {
            SignInRequestDTO request = new SignInRequestDTO("unknown@example.com", "password");
            when(userDetailsCustomService.loadUserByUsername(request.getEmail())).thenReturn(null);

            assertThrows(AppUserNotFoundException.class, () -> authService.signIn(request));
        }

        @Test
        void whenSignInWithIncorrectPassword_thenThrowIncorrectAuthDataException() {
            SignInRequestDTO request = new SignInRequestDTO("user@example.com", "wrongPassword");
            AppUser appUser = new AppUser();
            appUser.setEmail(request.getEmail());
            when(userDetailsCustomService.loadUserByUsername(request.getEmail())).thenReturn(appUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new AuthenticationException("Bad credentials") {});

            assertThrows(IncorrectAuthDataException.class, () -> authService.signIn(request));
        }
    }

    @Nested
    class GetAccessToken {
        @Test
        void whenGetAccessTokenWithValidRefreshToken_thenReturnAccessTokenResponseDTO() {
            String refreshToken = "validRefreshToken";
            String email = "user@example.com";
            AppUser appUser = new AppUser();
            appUser.setEmail(email);
            String accessToken = "newAccessToken";
            Claims claims = mock(Claims.class);

            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
            when(jwtService.getRefreshClaims(refreshToken)).thenReturn(claims);
            when(claims.getSubject()).thenReturn(email);
            when(redisService.getAuthValue(email)).thenReturn(refreshToken);
            when(userDetailsCustomService.loadUserByUsername(email)).thenReturn(appUser);
            when(jwtService.generateAccessToken(appUser)).thenReturn(accessToken);

            AccessTokenResponseDTO result = authService.getAccessToken(refreshToken);

            assertNotNull(result);
            assertEquals(accessToken, result.getAccessToken());
        }

        @Test
        void whenGetAccessTokenWithInvalidRefreshToken_thenThrowInvalidJwtTokenException() {
            String refreshToken = "invalidRefreshToken";
            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(false);

            assertThrows(InvalidJwtTokenException.class, () -> authService.getAccessToken(refreshToken));
        }

        @Test
        void whenGetAccessTokenWithNoStoredRefreshToken_thenThrowAppUserNotFoundException() {
            String refreshToken = "validRefreshToken";
            String email = "user@example.com";
            Claims claims = mock(Claims.class);

            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
            when(jwtService.getRefreshClaims(refreshToken)).thenReturn(claims);
            when(claims.getSubject()).thenReturn(email);
            when(redisService.getAuthValue(email)).thenReturn(null);

            assertThrows(AppUserNotFoundException.class, () -> authService.getAccessToken(refreshToken));
        }
    }

    @Nested
    class LogOut {
        @Test
        void whenLogOutWithValidRefreshToken_thenDeleteFromRedis() {
            String refreshToken = "validRefreshToken";
            String email = "user@example.com";
            Claims claims = mock(Claims.class);

            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
            when(jwtService.getRefreshClaims(refreshToken)).thenReturn(claims);
            when(claims.getSubject()).thenReturn(email);
            when(redisService.hasAuthKey(email)).thenReturn(true);

            authService.logOut(refreshToken);

            verify(redisService).deleteAuthValue(email);
        }

        @Test
        void whenLogOutWithInvalidRefreshToken_thenThrowInvalidJwtTokenException() {
            String refreshToken = "invalidRefreshToken";
            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(false);

            assertThrows(InvalidJwtTokenException.class, () -> authService.logOut(refreshToken));
        }

        @Test
        void whenLogOutWithNoStoredRefreshToken_thenThrowInvalidJwtTokenException() {
            String refreshToken = "validRefreshToken";
            String email = "user@example.com";
            Claims claims = mock(Claims.class);

            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
            when(jwtService.getRefreshClaims(refreshToken)).thenReturn(claims);
            when(claims.getSubject()).thenReturn(email);
            when(redisService.hasAuthKey(email)).thenReturn(false);

            assertThrows(InvalidJwtTokenException.class, () -> authService.logOut(refreshToken));
        }
    }

    @Nested
    class ChangePassword {
        @Test
        void whenChangePasswordWithCorrectOldPassword_thenUpdatePassword() {
            AppUser appUser = new AppUser();
            appUser.setPasswordHash("oldEncodedPassword");
            ChangePasswordRequestDTO request = new ChangePasswordRequestDTO("oldPassword", "newPassword");

            when(passwordEncoder.matches("oldPassword", "oldEncodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

            authService.changePassword(appUser, request);

            assertEquals("newEncodedPassword", appUser.getPasswordHash());
            verify(appUserService).save(appUser);
        }

        @Test
        void whenChangePasswordWithIncorrectOldPassword_thenThrowIncorrectPasswordException() {
            AppUser appUser = new AppUser();
            appUser.setPasswordHash("oldEncodedPassword");
            ChangePasswordRequestDTO request = new ChangePasswordRequestDTO("wrongPassword", "newPassword");

            when(passwordEncoder.matches("wrongPassword", "oldEncodedPassword")).thenReturn(false);

            assertThrows(IncorrectPasswordException.class, () -> authService.changePassword(appUser, request));
        }
    }

    @Nested
    class RequestResetPassword {
        @Test
        void whenRequestResetPasswordWithExistingEmail_thenSetTokenAndNotify() {
            String email = "user@example.com";
            AppUser appUser = new AppUser();
            appUser.setEmail(email);
            String resetToken = "resetToken";

            when(appUserService.findByEmail(email)).thenReturn(appUser);
            when(jwtService.generateResetPasswordToken(appUser)).thenReturn(resetToken);
            when(notificationContext.getMessageFactory()).thenReturn(messageFactory);
            when(notificationContext.getMessageFactory().getResetPasswordMessage(resetToken)).thenReturn("message");

            authService.requestResetPassword(email);

            verify(redisService).setResetPasswordValue(email, resetToken);
            verify(notificationContext).setNotificationType(NotificationType.EMAIL);
            verify(notificationContext).notifyUser(email, "Orbistay Reset Password", "message");
        }

        @Test
        void whenRequestResetPasswordWithNonExistingEmail_thenThrowAppUserNotFoundException() {
            String email = "unknown@example.com";
            when(appUserService.findByEmail(email)).thenThrow(new AppUserNotFoundException("User not found"));

            assertThrows(AppUserNotFoundException.class, () -> authService.requestResetPassword(email));
        }
    }

    @Nested
    class ResetPassword {
        @Test
        void whenResetPasswordWithValidToken_thenUpdatePasswordAndDeleteToken() {
            String token = "validToken";
            String newPassword = "newPassword";
            String email = "user@example.com";
            AppUser appUser = new AppUser();
            appUser.setEmail(email);
            Claims claims = mock(Claims.class);

            when(jwtService.validateResetPasswordToken(token)).thenReturn(true);
            when(jwtService.getResetPasswordClaims(token)).thenReturn(claims);
            when(claims.getSubject()).thenReturn(email);
            when(appUserService.findByEmail(email)).thenReturn(appUser);
            when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

            authService.resetPassword(token, newPassword);

            assertEquals("encodedNewPassword", appUser.getPasswordHash());
            verify(appUserService).save(appUser);
            verify(redisService).deleteResetPasswordValue(email);
        }

        @Test
        void whenResetPasswordWithInvalidToken_thenThrowInvalidJwtTokenException() {
            String token = "invalidToken";
            String newPassword = "newPassword";
            when(jwtService.validateResetPasswordToken(token)).thenReturn(false);

            assertThrows(InvalidJwtTokenException.class, () -> authService.resetPassword(token, newPassword));
        }
    }

    @Nested
    class VerifyEmail {
        @Test
        void whenVerifyEmailWithValidToken_thenSetEmailVerified() {
            String token = "validToken";
            EmailVerification verification = new EmailVerification();
            verification.setExpiresAt(LocalDateTime.now().plusDays(1));
            verification.setVerified(false);
            AppUser appUser = new AppUser();
            verification.setAppUser(appUser);

            when(emailVerificationService.findByToken(token)).thenReturn(verification);

            authService.verifyEmail(token);

            verify(emailVerificationService).changeAndSaveEmailVerificationToVerified(verification);
        }

        @Test
        void whenVerifyEmailWithExpiredToken_thenThrowEmailVerificationExpiredException() {
            String token = "expiredToken";
            EmailVerification verification = new EmailVerification();
            verification.setExpiresAt(LocalDateTime.now().minusDays(1));
            verification.setVerified(false);

            when(emailVerificationService.findByToken(token)).thenReturn(verification);

            assertThrows(EmailVerificationExpiredException.class, () -> authService.verifyEmail(token));
        }
    }

    @Nested
    class ResendVerificationEmail {
        @Test
        void whenResendVerificationEmailForUnverifiedUser_thenSendNotification() {
            AppUser appUser = new AppUser();
            appUser.setEmail("user@example.com");
            EmailVerification verification = new EmailVerification();
            verification.setVerified(false);
            verification.setAppUser(appUser);
            appUser.setEmailVerification(verification);

            when(emailVerificationService.continueAndSaveVerification(verification)).thenReturn(verification);
            when(notificationContext.getMessageFactory()).thenReturn(messageFactory);
            when(messageFactory.getVerificationMessage(any(AppUser.class))).thenReturn("message");

            authService.resendVerificationEmail(appUser);

            verify(notificationContext).setNotificationType(NotificationType.EMAIL);
            verify(notificationContext).notifyUser(appUser.getEmail(), "Orbistay Email Verification", "message");
        }

        @Test
        void whenResendVerificationEmailForVerifiedUser_thenThrowEmailAlreadyVerifiedException() {
            AppUser appUser = new AppUser();
            EmailVerification verification = new EmailVerification();
            verification.setVerified(true);
            appUser.setEmailVerification(verification);

            assertThrows(EmailAlreadyVerifiedException.class, () -> authService.resendVerificationEmail(appUser));
        }
    }
}