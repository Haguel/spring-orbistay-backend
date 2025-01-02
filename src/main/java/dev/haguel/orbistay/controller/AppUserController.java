package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.JwtAccessTokenDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.exception.InvalidJwtTokenException;
import dev.haguel.orbistay.service.AppUserService;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.mapper.AppUserMapperImpl;
import dev.haguel.orbistay.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app-user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "App user")
public class AppUserController {
    private final AppUserService appUserService;
    private final JwtService jwtService;

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
    public ResponseEntity<?> getCurrentAppUserInfo(@RequestHeader(name="Authorization") String authorizationHeader) throws AppUserNotFoundException, InvalidJwtTokenException {
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

        return ResponseEntity.status(200).body(AppUserMapperImpl.INSTANCE.appUserToAppUserInfoDTO(appUser));
    }
}
