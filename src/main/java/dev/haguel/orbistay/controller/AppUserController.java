package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.exception.InvalidJwtTokenException;
import dev.haguel.orbistay.mapper.AppUserMapper;
import dev.haguel.orbistay.service.AppUserService;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app-user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "App user")
public class AppUserController {
    private final AppUserService appUserService;
    private final JwtService jwtService;
    private final AppUserMapper appUserMapper;

    @Operation(summary = "Get current app user info by jwt access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "App user info returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAppUserInfoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "App user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Access token doesn't contain user email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get/current")
    public ResponseEntity<?> getCurrentAppUserInfo(@RequestHeader(name="Authorization") String authorizationHeader)
            throws AppUserNotFoundException, InvalidJwtTokenException {
        log.info("Get current app user info request received");
        String jwtToken = SecurityUtil.getTokenFromAuthorizationHeader(authorizationHeader);
        String appUserEmail = jwtService.getAccessClaims(jwtToken).getSubject();

        if(appUserEmail == null) {
            throw new InvalidJwtTokenException("Access token doesn't contain user email");
        }

        AppUser appUser = appUserService.findByEmail(appUserEmail);

        if(appUser == null) {
            throw new AppUserNotFoundException("App user couldn't be found in database by provided email");
        }

        return ResponseEntity.status(200).body(appUserMapper.appUserToAppUserInfoDTO(appUser));
    }

    @Operation(summary = "Edit current app user data by jwt access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "App user data edited successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAppUserInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Request body validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "App user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Access token doesn't contain user email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/edit/current")
    public ResponseEntity<?> editAppUserData(@RequestHeader(name="Authorization") String authorizationHeader, @Valid @RequestBody EditAppUserDataRequestDTO data)
            throws AppUserNotFoundException, CountryNotFoundException, InvalidJwtTokenException {
        log.info("Edit app user data request received");
        String jwtToken = SecurityUtil.getTokenFromAuthorizationHeader(authorizationHeader);
        String appUserEmail = jwtService.getAccessClaims(jwtToken).getSubject();

        if(appUserEmail == null) {
            throw new InvalidJwtTokenException("Access token doesn't contain user email");
        }

        AppUser appUser = appUserService.findByEmail(appUserEmail);

        if(appUser == null) {
            throw new AppUserNotFoundException("App user couldn't be found in database by provided email");
        }

        appUser = appUserService.editAppUserData(appUser, data);

        return ResponseEntity.status(200).body(appUserMapper.appUserToAppUserInfoDTO(appUser));
    }
}
