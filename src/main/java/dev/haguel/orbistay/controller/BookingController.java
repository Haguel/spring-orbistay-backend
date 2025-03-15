package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.dto.response.BookingInfoResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.Country;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.mapper.BookingMapper;
import dev.haguel.orbistay.service.BookingService;
import dev.haguel.orbistay.service.SecurityService;
import dev.haguel.orbistay.util.EndPoints;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking")
public class BookingController {
    private final BookingService bookingService;
    private final SecurityService securityService;
    private final BookingMapper bookingMapper;

    @Schema(description = "Book hotel room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel room booked successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Check in must be before check out",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Required data must be filled",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Passport is expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Hotel room not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Booking not available",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Booking.BOOK_HOTEL_ROOM)
    public ResponseEntity<?> bookHotelRoom(@RequestHeader(name="Authorization") String authorizationHeader,
                                           @Valid @RequestBody BookHotelRoomRequestDTO bookHotelRoomRequestDTO)
            throws InvalidJwtTokenException, HotelRoomNotFoundException, BookingNotAvailableException, CountryNotFoundException, InvalidDataException {
        log.info("Book hotel room request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        Booking booking = bookingService.bookHotelRoom(appUser, bookHotelRoomRequestDTO);

        return ResponseEntity.status(200).body(bookingMapper.bookingToBookingInfoResponseDTO(booking));
    }

    @Schema(description = "Get bookings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingInfoResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(EndPoints.Booking.GET_BOOKINGS)
    public ResponseEntity<?> getBookings(@RequestHeader(name="Authorization") String authorizationHeader)
            throws InvalidJwtTokenException {
        log.info("Get bookings request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        List<Booking> bookings = appUser.getBookings();
        log.info("Found {} bookings", bookings.size());

        List<BookingInfoResponseDTO> bookingInfoResponseDTOs = bookings.stream()
                .map(bookingMapper::bookingToBookingInfoResponseDTO).toList();

        log.info("Returning bookings");
        return ResponseEntity.status(200).body(bookingInfoResponseDTOs);
    }

    @Schema(description = "Cancel booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking canceled successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid jwt token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Booking can not be canceled",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Can not change other user data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(EndPoints.Booking.CANCEL_BOOKING + "/{id}")
    public ResponseEntity<?> cancelBooking(@RequestHeader(name="Authorization") String authorizationHeader,
                                          @PathVariable Long id)
            throws InvalidJwtTokenException, BookingNotFoundException, BookingCanNotBeCanceledException, CanNotChangeOtherUserDataException {
        log.info("Cancel booking request received");
        AppUser appUser = securityService.getAppUserFromAuthorizationHeader(authorizationHeader);
        Booking booking = bookingService.findById(id);
        bookingService.cancelBooking(appUser, booking);

        log.info("Booking canceled successfully");
        return ResponseEntity.status(200).body(booking);
    }
}
