package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.dto.response.GetFilteredHotelsResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.request.GetFilteredHotelsRequestDTO;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.exception.error.ErrorResponse;
import dev.haguel.orbistay.service.HotelRoomService;
import dev.haguel.orbistay.service.HotelService;
import dev.haguel.orbistay.service.ReviewService;
import dev.haguel.orbistay.service.SecurityService;
import dev.haguel.orbistay.util.EndPoints;
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
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetFilteredHotelsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No hotels found for given criteria",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Hotels.GET_FILTERED_HOTELS)
    public ResponseEntity<?> getFilteredHotels(@RequestBody @Valid GetFilteredHotelsRequestDTO getFilteredHotelsRequestDTO)
            throws HotelsNotFoundException {
        log.info("Get hotels request received");
        GetFilteredHotelsResponseDTO getFilteredHotelsResponseDTO = hotelService.getFilteredHotels(getFilteredHotelsRequestDTO);

        log.info("Hotels returned");
        return ResponseEntity.status(200).body(getFilteredHotelsResponseDTO);
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
    @GetMapping(EndPoints.Hotels.GET_HOTEL + "/{id}")
    public ResponseEntity<?> getHotel(@PathVariable Long id)
            throws HotelNotFoundException {
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
    @PostMapping(EndPoints.Hotels.GET_FILTERED_HOTEL_ROOMS)
    public ResponseEntity<?> getFilteredHotelRooms(@RequestBody @Valid GetFileredHotelRoomsRequestDTO getFileredHotelRoomsRequestDTO)
            throws HotelRoomsNotFoundException {
        log.info("Get hotel rooms request received");
        List<HotelRoom> hotelRooms = hotelRoomService.getFilteredHotelRooms(getFileredHotelRoomsRequestDTO);

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
    @GetMapping(EndPoints.Hotels.GET_HOTEL_ROOM + "/{id}")
    public ResponseEntity<?> getHotelRoom(@PathVariable Long id)
            throws HotelRoomNotFoundException {
        log.info("Get hotel room by id request received");
        HotelRoom hotelRoom = hotelRoomService.findById(id);

        log.info("Hotel room returned");
        return ResponseEntity.status(200).body(hotelRoom);
    }

    @Operation(summary = "Write hotel review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review written successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Hotels.WRITE_HOTEL_REVIEW)
    public ResponseEntity<?> writeReview(@RequestHeader("Authorization") String token,
                                         @RequestBody @Valid WriteReviewRequestDTO writeReviewRequestDTO)
            throws InvalidJwtTokenException, HotelNotFoundException {
        log.info("Write review request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(token);
        Review review = reviewService.save(appUser, writeReviewRequestDTO);

        log.info("Review returned");
        return ResponseEntity.status(201).body(review);
    }

    @Operation(summary = "Get hotel reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Hotels.GET_HOTEL_REVIEWS + "/{hotelId}")
    public ResponseEntity<?> getReviews(@PathVariable Long hotelId) throws HotelNotFoundException {
        log.info("Get reviews request received");
        Hotel hotel = hotelService.findById(hotelId);
        List<Review> reviews = hotel.getReviews();
        log.info("Found {} reviews", reviews.size());

        log.info("Reviews returned");
        return ResponseEntity.status(200).body(reviews);
    }

    @Operation(summary = "Remove hotel review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid JWT token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Can not change other user data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(EndPoints.Hotels.REMOVE_HOTEL_REVIEW + "/{reviewId}")
    public ResponseEntity<?> removeReview(@RequestHeader("Authorization") String token,
                                          @PathVariable Long reviewId)
            throws InvalidJwtTokenException, ReviewNotFoundException, CanNotChangeOtherUserDataException {
        log.info("Remove review request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(token);
        Review review = reviewService.findById(reviewId);
        reviewService.delete(appUser, review);

        log.info("Review removed");
        return ResponseEntity.status(200).build();
    }

    @Operation(summary = "Get popular hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Popular hotels found successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetHotelsResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Hotels.GET_POPULAR_HOTELS)
    public ResponseEntity<?> getPopularHotels() {
        log.info("Get popular hotels request received");
        List<GetHotelsResponseDTO> hotels = hotelService.getPopularHotels();

        log.info("Popular hotels returned");
        return ResponseEntity.status(200).body(hotels);
    }
}
