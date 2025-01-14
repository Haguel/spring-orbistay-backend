package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Favorites;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.CanNotChangeOtherUserDataException;
import dev.haguel.orbistay.exception.FavoritesNotFoundException;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.exception.InvalidJwtTokenException;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.mapper.HotelMapper;
import dev.haguel.orbistay.service.FavoritesService;
import dev.haguel.orbistay.service.HotelService;
import dev.haguel.orbistay.service.SecurityService;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Favorites")
public class FavoritesController {
    private final FavoritesService favoritesService;
    private final HotelService hotelService;
    private final SecurityService securityService;
    private final HotelMapper hotelMapper;

    @Operation(description = "Add hotel to favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel added to favorites successfully",
                            content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Favorites.ADD_TO_FAVORITES + "/{hotelId}")
    public ResponseEntity<?> addHotelToFavorites(@RequestHeader(name="Authorization") String authorizationHeader,
                                                 @PathVariable String hotelId)
            throws HotelNotFoundException, InvalidJwtTokenException {
        log.info("Add hotel to favorites request received");
        Hotel hotel = hotelService.findById(Long.parseLong(hotelId));
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);

        favoritesService.addHotelToFavorites(appUser, hotel);
        return ResponseEntity.status(201).build();
    }

    @Operation(description = "Get favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorites retrieved successfully",
                            content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Favorites.GET_FAVORITES)
    public ResponseEntity<?> getFavorites(@RequestHeader(name="Authorization") String authorizationHeader)
            throws InvalidJwtTokenException {
        log.info("Get favorites request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        List<GetHotelsResponseDTO> favorites = appUser.getMappedFavoriteHotels()
                .stream().map(hotelMapper::hotelToHotelsResponseDTO).toList();
        log.info("Found {} favorites", favorites.size());

        return ResponseEntity.status(200).body(favorites);
    }

    @Operation(description = "Remove hotel from favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel removed from favorites successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Can not delete other user's favorites",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Favorites not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(EndPoints.Favorites.REMOVE_FAVORITES + "/{favoritesId}")
    public ResponseEntity<?> removeHotelFromFavorites(@RequestHeader(name="Authorization") String authorizationHeader,
                                                      @PathVariable String favoritesId)
            throws InvalidJwtTokenException, CanNotChangeOtherUserDataException, FavoritesNotFoundException {
        log.info("Remove hotel from favorites request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        Favorites favorites = favoritesService.findById(Long.parseLong(favoritesId));

        favoritesService.removeHotelFromFavorites(appUser, favorites);
        return ResponseEntity.status(200).build();
    }
}
