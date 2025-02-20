package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.ChangePasswordRequestDTO;
import dev.haguel.orbistay.dto.request.SignInRequestDTO;
import dev.haguel.orbistay.dto.request.SignUpRequestDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.EmailVerification;
import dev.haguel.orbistay.entity.enumeration.Role;
import dev.haguel.orbistay.exception.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserService appUserService;
    private final UserDetailsCustomService userDetailsCustomService;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    private JwtResponseDTO getJwtResponseDTO(AppUser appUser) {
        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        redisService.setAuthValue(appUser.getEmail(), refreshToken);

        return new JwtResponseDTO(accessToken, refreshToken);
    }

    @Transactional(noRollbackFor = {EmailSendingException.class})
    public JwtResponseDTO signUp(SignUpRequestDTO signUpRequestDTO)
            throws UniquenessViolationException, EmailSendingException {
        AppUser appUser = AppUser.builder()
                .username(signUpRequestDTO.getUsername())
                .email(signUpRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(signUpRequestDTO.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        try {
            appUser = appUserService.save(appUser);
            appUser.setEmailVerification(emailService.createNeededVerificationForAppUser(appUser));
            appUser = appUserService.save(appUser);
        } catch (DataIntegrityViolationException exception) {
            if(exception.getMessage().contains("app_user_username_key")) {
                throw new UniquenessViolationException("Username already exists");
            }
            if(exception.getMessage().contains("app_user_email_key")) {
                throw new UniquenessViolationException("Email already exists");
            }
        }

        emailService.sendVerificationEmail(appUser);

        return getJwtResponseDTO(appUser);
    }

    public JwtResponseDTO signIn(SignInRequestDTO signInRequestDTO)
            throws AppUserNotFoundException, IncorrectAuthDataException {
        String email = signInRequestDTO.getEmail();
        UserDetails appUser = userDetailsCustomService.loadUserByUsername(email);
        if(appUser == null) {
            throw new AppUserNotFoundException("User not found");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequestDTO.getEmail(),
                    signInRequestDTO.getPassword()
            ));
        } catch (AuthenticationException exception) {
            throw new IncorrectAuthDataException("Incorrect email or password");
        }

        return getJwtResponseDTO((AppUser) appUser);
    }

    public JwtResponseDTO getAccessToken(String refreshToken)
            throws AppUserNotFoundException, InvalidJwtTokenException {
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw new InvalidJwtTokenException("Invalid refresh token");
        }

        Claims claims = jwtService.getRefreshClaims(refreshToken);
        String email = claims.getSubject();
        String storedRefreshToken = redisService.getAuthValue(email);

        if (storedRefreshToken == null) {
            log.error("Refresh token doesn't bind to any email in redis");
            throw new AppUserNotFoundException("User not found");
        }

        UserDetails userDetails = userDetailsCustomService.loadUserByUsername(email);
        String accessToken = jwtService.generateAccessToken(userDetails);

        return new JwtResponseDTO(accessToken, null);
    }

    public JwtResponseDTO refresh(String refreshToken)
            throws AppUserNotFoundException, InvalidJwtTokenException {
        if (jwtService.validateRefreshToken(refreshToken)) {
            Claims claims = jwtService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String storedRefreshToken = redisService.getAuthValue(email);

            if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
                final AppUser appUser = appUserService.findByEmail(email);
                if(appUser == null) {
                    throw new AppUserNotFoundException("User not found");
                }

                return getJwtResponseDTO(appUser);
            }
        }

        throw new InvalidJwtTokenException("Invalid refresh token");
    }

    public void logOut(String refreshToken)
            throws InvalidJwtTokenException {
        if(jwtService.validateRefreshToken(refreshToken)) {
            Claims claims = jwtService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();

            if(!redisService.hasAuthKey(email)) {
                log.error("Refresh token doesn't bind to any email in redis");
                throw new InvalidJwtTokenException("Invalid refresh token");
            }

            redisService.deleteAuthValue(email);
        } else {
            throw new InvalidJwtTokenException("Invalid refresh token");
        }
    }

    public void changePassword(AppUser appUser, ChangePasswordRequestDTO changePasswordRequestDTO)
            throws IncorrectPasswordException {
        if (passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), appUser.getPasswordHash())) {
            appUser.setPasswordHash(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));

            appUserService.save(appUser);
        } else {
            throw new IncorrectPasswordException("Incorrect old password");
        }
    }

    @Transactional(readOnly = true)
    public void requestResetPassword(String email)
            throws EmailSendingException {
        AppUser appUser = appUserService.findByEmail(email);

        String resetPasswordJwt = jwtService.generateResetPasswordToken(appUser);
        redisService.setResetPasswordValue(appUser.getEmail(), resetPasswordJwt);

        emailService.sendResetPasswordEmail(appUser, resetPasswordJwt);
    }

    @Transactional
    public void resetPassword(String token, String newPassword)
            throws InvalidJwtTokenException, AppUserNotFoundException {
        if(!jwtService.validateResetPasswordToken(token)) {
            throw new InvalidJwtTokenException("Invalid reset password token");
        }

        Claims claims = jwtService.getResetPasswordClaims(token);
        String email = claims.getSubject();
        AppUser appUser = appUserService.findByEmail(email);

        appUser.setPasswordHash(passwordEncoder.encode(newPassword));
        appUserService.save(appUser);

        redisService.deleteResetPasswordValue(email);
    }

    public void verifyEmail(String token) {
        EmailVerification emailVerification = emailService.findByToken(token);

        if(!emailVerification.isVerified() && emailVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EmailVerificationExpiredException("Email verification is expired");
        }

        AppUser appUser = emailVerification.getAppUser();
        if(appUser == null) {
            log.error("Email verification doesn't bind to any user");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Email verification doesn't bind to any user");
        }

        emailService.changeAndSaveEmailVerificationToVerified(emailVerification);
    }

    public void resendVerificationEmail(AppUser appUser)
            throws EmailSendingException {
        EmailVerification emailVerification = appUser.getEmailVerification();
        if(emailVerification.isVerified()) {
            log.warn("Can't send verification email to already verified user");
            throw new EmailAlreadyVerifiedException("Email already verified");
        }

        emailVerification = emailService.continueAndSaveVerification(emailVerification);
        appUser = emailVerification.getAppUser();

        emailService.sendVerificationEmail(appUser);
    }
}
