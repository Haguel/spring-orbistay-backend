package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.*;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.dto.response.JwtDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.service.AuthService;
import dev.haguel.orbistay.service.SecurityService;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;
    private final SecurityService securityService;
    @Value("${refresh.cookie.same-site}")
    private String sameSite;

    @Operation(summary = "Sign up. Http request credentials required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User signed up successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Field uniqueness violation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.SIGN_UP)
    public ResponseEntity<AccessTokenResponseDTO> signUp(@RequestBody @Valid SignUpRequestDTO signUpRequestDTO,
                                                         HttpServletRequest httpServletRequest)
            throws UniquenessViolationException, EmailSendingException {
        log.info("Sign up request received");
        JwtDTO jwtDTO = authService.signUp(signUpRequestDTO);
        AccessTokenResponseDTO accessTokenResponseDTO = AccessTokenResponseDTO.builder()
                .accessToken(jwtDTO.getAccessToken())
                .build();

        boolean isSecure = httpServletRequest.isSecure();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", jwtDTO.getRefreshToken())
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(Duration.ofDays(30))
                .build();

        log.info("User signed up successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Set-Cookie", refreshCookie.toString())
                .body(accessTokenResponseDTO);
    }

    @Operation(summary = "Sign in. Http request credentials required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed in successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Incorrect email or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequestDTO signInRequestDTO,
                                    HttpServletRequest httpServletRequest)
            throws AppUserNotFoundException, IncorrectAuthDataException {
        log.info("Sign in request received");
        JwtDTO jwtDTO = authService.signIn(signInRequestDTO);
        AccessTokenResponseDTO accessTokenResponseDTO = AccessTokenResponseDTO.builder()
                .accessToken(jwtDTO.getAccessToken())
                .build();

        boolean isSecure = httpServletRequest.isSecure();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", jwtDTO.getRefreshToken())
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(Duration.ofDays(30))
                .build();

        log.info("User signed in successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie", refreshCookie.toString())
                .body(accessTokenResponseDTO);
    }

    @Operation(summary = "Get new JWT access token. Http request credentials required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.REFRESH_ACCESS_TOKEN)
    public ResponseEntity<?> getNewAccessToken(@CookieValue(name = "refresh_token") String refreshToken)
            throws AppUserNotFoundException, InvalidJwtTokenException {
        log.info("Refresh access token request received");
        AccessTokenResponseDTO accessTokenResponseDTO = authService.getAccessToken(refreshToken);

        log.info("Access token refreshed successfully");
        return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponseDTO);
    }

    @Operation(summary = "Log out. Http request credentials required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.LOG_OUT)
    public ResponseEntity<?> logOut(@CookieValue(name = "refresh_token") String refreshToken)
            throws InvalidJwtTokenException {
        log.info("Log out request received");
        authService.logOut(refreshToken);

        log.info("User logged out successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Change password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Password is incorrect",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestHeader(name = "Authorization") String authorizationHeader,
                                            @RequestBody @Valid ChangePasswordRequestDTO changePasswordRequestDTO)
            throws InvalidJwtTokenException, IncorrectPasswordException {
        log.info("Change password request received");

        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        authService.changePassword(appUser, changePasswordRequestDTO);

        log.info("Password changed successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Verify email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "401", description = "Email can't be verified because verification email has been expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Email verification not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token)
            throws EmailVerificationNotFoundException, EmailVerificationExpiredException {
        log.info("Email verification request received");

        authService.verifyEmail(token);

        log.info("Email verified successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Resend email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verification resent successfully"),
            @ApiResponse(responseCode = "400", description = "Email verification already verified",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Email verification not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.RESEND_EMAIL_VERIFICATION)
    public ResponseEntity<?> resendEmailVerification(@RequestHeader(name = "Authorization") String authorizationHeader)
            throws EmailVerificationNotFoundException, EmailSendingException {
        log.info("Resend email verification request received");

        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        authService.resendVerificationEmail(appUser);

        log.info("Email verification resent successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Request reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset password request sent successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.REQUEST_RESET_PASSWORD)
    public ResponseEntity<?> requestResetPassword(@RequestBody @Valid RequestPasswordResetRequestDTO requestPasswordResetRequestDTO)
            throws AppUserNotFoundException, EmailSendingException {
        log.info("Request reset password request received");

        authService.requestResetPassword(requestPasswordResetRequestDTO.getEmail());

        log.info("Reset password request sent successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Auth.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetRequestDTO passwordResetRequestDTO)
            throws InvalidJwtTokenException, AppUserNotFoundException {
        log.info("Reset password request received");

        authService.resetPassword(passwordResetRequestDTO.getResetPasswordJwtToken(), passwordResetRequestDTO.getNewPassword());

        log.info("Password reset successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
