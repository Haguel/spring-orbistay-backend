package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.response.EditAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.EditAppUserInfoResponseWrapperDTO;
import dev.haguel.orbistay.dto.response.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.exception.InvalidJwtTokenException;
import dev.haguel.orbistay.mapper.AppUserMapper;
import dev.haguel.orbistay.service.AppUserService;
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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "App user")
public class AppUserController {
    private final AppUserService appUserService;
    private final SecurityService securityService;
    private final AppUserMapper appUserMapper;
    @Value("${refresh.cookie.same-site}")
    private String sameSite;

    @Operation(summary = "Get current app user info by jwt access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "App user info returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAppUserInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Access token doesn't contain user email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.AppUsers.GET_CURRENT_APP_USER)
    public ResponseEntity<?> getCurrentAppUserInfo(@RequestHeader(name="Authorization") String authorizationHeader)
            throws InvalidJwtTokenException {
        log.info("Get current app user info request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);

        log.info("App user info returned successfully");
        return ResponseEntity.status(200).body(appUserMapper.appUserToAppUserInfoDTO(appUser));
    }

    @Operation(summary = "Edit current app user data by jwt access token. Http request credentials required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "App user data edited successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EditAppUserInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Request body validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Jwt token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(EndPoints.AppUsers.EDIT_CURRENT_APP_USER)
    public ResponseEntity<?> editAppUserData(@RequestHeader(name="Authorization") String authorizationHeader,
                                             @Valid @RequestBody EditAppUserDataRequestDTO data,
                                             HttpServletRequest httpServletRequest)
            throws CountryNotFoundException, InvalidJwtTokenException {
        log.info("Edit app user data request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        EditAppUserInfoResponseWrapperDTO editAppUserInfoResponseWrapperDTO = appUserService.editAppUserData(appUser, data);

        log.info("EditAppUserInfoResponseDTO: {}", editAppUserInfoResponseWrapperDTO.getEditAppUserInfoResponseDTO());

        boolean isSecure = httpServletRequest.isSecure();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", editAppUserInfoResponseWrapperDTO.getJwtDTO().getRefreshToken())
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(Duration.ofDays(30))
                .build();

        log.info("App user data edited successfully");
        return ResponseEntity.status(200)
                .header("Set-Cookie", refreshCookie.toString())
                .body(editAppUserInfoResponseWrapperDTO.getEditAppUserInfoResponseDTO());
    }

    @Operation(summary = "Upload avatar for current app user by jwt access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAppUserInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Jwt token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.AppUsers.UPLOAD_AVATAR)
    public ResponseEntity<?> uploadAvatar(@RequestHeader(name="Authorization") String authorizationHeader,
                                          @RequestParam("avatar") MultipartFile avatar)
            throws InvalidJwtTokenException, IOException {
        log.info("Upload avatar request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        appUser = appUserService.setAvatar(appUser, avatar);

        log.info("Avatar uploaded successfully");
        return ResponseEntity.status(200).body(appUserMapper.appUserToAppUserInfoDTO(appUser));
    }
}
