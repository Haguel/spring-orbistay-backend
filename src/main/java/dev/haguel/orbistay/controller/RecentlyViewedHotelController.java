package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.AddToRecentlyViewedHotelsRequestDTO;
import dev.haguel.orbistay.dto.response.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.exception.InvalidJwtTokenException;
import dev.haguel.orbistay.service.RecentlyViewedHotelService;
import dev.haguel.orbistay.service.SecurityService;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RecentlyViewedHotelController {
    private final SecurityService securityService;
    private final RecentlyViewedHotelService recentlyViewedHotelService;

    @Operation(summary = "Get recently viewed hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "App user info returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetHotelsResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.RecentlyViewedHotels.GET_RECENTLY_VIEWED_HOTELS)
    public ResponseEntity<?> getRecentlyViewedHotels(@RequestHeader("Authorization") String token)
            throws InvalidJwtTokenException {
        log.info("Get recently viewed hotels request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(token);
        List<GetHotelsResponseDTO> hotels = recentlyViewedHotelService.getRecentlyViewedHotels(appUser);

        log.info("Recently viewed hotels returned");
        return ResponseEntity.status(200).body(hotels);
    }

    @Operation(summary = "Add to recently viewed hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel added to recently viewed hotels",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.RecentlyViewedHotels.ADD_TO_RECENTLY_VIEWED_HOTELS)
    public ResponseEntity<?> addToRecentlyViewedHotels(@RequestHeader("Authorization") String token,
                                                       @Valid @RequestBody AddToRecentlyViewedHotelsRequestDTO addToRecentlyViewedHotelsRequestDTO)
            throws InvalidJwtTokenException, HotelNotFoundException {
        log.info("Add to recently viewed hotels request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(token);
        recentlyViewedHotelService.addToRecentlyViewedHotels(appUser, Long.parseLong(addToRecentlyViewedHotelsRequestDTO.getHotelId()));

        log.info("Hotel added to recently viewed hotels");
        return ResponseEntity.status(200).build();
    }
}
