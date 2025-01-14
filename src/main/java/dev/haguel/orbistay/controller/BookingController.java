package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.service.BookingService;
import dev.haguel.orbistay.service.SecurityService;
import dev.haguel.orbistay.util.EndPoints;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking")
public class BookingController {
    private final BookingService bookingService;
    private final SecurityService securityService;

    @Schema(description = "Book hotel room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel room booked successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Hotel room not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Booking not available",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(EndPoints.Booking.BOOK_HOTEL_ROOM)
    public ResponseEntity<?> bookHotelRoom(@RequestHeader(name="Authorization") String authorizationHeader,
                                           @Valid @RequestBody BookHotelRoomRequestDTO bookHotelRoomRequestDTO)
            throws InvalidJwtTokenException, HotelRoomNotFoundException, BookingNotAvailableException, CountryNotFoundException {
        log.info("Book hotel room request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        Booking booking = bookingService.bookHotelRoom(appUser, bookHotelRoomRequestDTO);

        return ResponseEntity.status(200).body(booking);
    }
}
