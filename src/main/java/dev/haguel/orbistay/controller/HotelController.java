package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.request.GetHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.request.GetHotelsRequestDTO;
import dev.haguel.orbistay.dto.response.GetHotelsIncludeRoomResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.service.HotelRoomService;
import dev.haguel.orbistay.service.HotelService;
import dev.haguel.orbistay.service.ReviewService;
import dev.haguel.orbistay.service.SecurityService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotel")
public class HotelController {
    private final HotelService hotelService;
    private final HotelRoomService hotelRoomService;
    private final ReviewService reviewService;
    private final SecurityService securityService;

    @Operation(summary = "Get hotels by criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetHotelsIncludeRoomResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No hotels found for given criteria",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get/filter")
    public ResponseEntity<?> getHotels(@RequestBody @Valid GetHotelsRequestDTO getHotelsRequestDTO) throws HotelsNotFoundException {
        log.info("Get hotels request received");
        List<GetHotelsIncludeRoomResponseDTO> hotels = hotelService.getHotels(getHotelsRequestDTO);

        log.info("Hotels returned");
        return ResponseEntity.status(200).body(hotels);
    }

    @Operation(summary = "Get hotel by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetHotelResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getHotel(@PathVariable Long id) throws HotelNotFoundException {
        log.info("Get hotel by id request received");
        GetHotelResponseDTO hotel = hotelService.getHotelById(id);

        log.info("Hotel returned");
        return ResponseEntity.status(200).body(hotel);
    }

    @Operation(summary = "Get hotel rooms by criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel rooms found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = HotelRoom.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No hotel rooms found for given criteria",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/room/get/filter")
    public ResponseEntity<?> getHotelRooms(@RequestBody @Valid GetHotelRoomsRequestDTO getHotelRoomsRequestDTO) throws HotelRoomsNotFoundException {
        log.info("Get hotel rooms request received");
        List<HotelRoom> hotelRooms = hotelRoomService.getHotelRooms(getHotelRoomsRequestDTO);

        log.info("Hotel rooms returned");
        return ResponseEntity.status(200).body(hotelRooms);
    }

    @Operation(summary = "Get hotel room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel room found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = HotelRoom.class))),
            @ApiResponse(responseCode = "404", description = "Hotel room not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/room/get/{id}")
    public ResponseEntity<?> getHotelRoom(@PathVariable Long id) throws HotelRoomNotFoundException {
        log.info("Get hotel room by id request received");
        HotelRoom hotelRoom = hotelRoomService.getHotelRoomById(id);

        log.info("Hotel room returned");
        return ResponseEntity.status(200).body(hotelRoom);
    }

    @Operation(summary = "Write review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review written successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/review")
    public ResponseEntity<?> writeReview(@RequestHeader("Authorization") String token,
                                         @RequestBody @Valid WriteReviewRequestDTO writeReviewRequestDTO)
            throws InvalidJwtTokenException, AppUserNotFoundException, HotelNotFoundException {
        log.info("Write review request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(token);
        Review review = reviewService.save(appUser, writeReviewRequestDTO);

        log.info("Review returned");
        return ResponseEntity.status(201).body(review);
    }
}
