package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.request.BookingPaymentRequestDTO;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.service.PaymentService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Schema(description = "Booking payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking paid successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(EndPoints.Payment.PAY_BOOKING)
    public ResponseEntity<?> payBooking(@Valid @RequestBody BookingPaymentRequestDTO bookingPaymentRequestDTO) {
        log.info("Booking payment request received");
        paymentService.payBooking(bookingPaymentRequestDTO);

        log.info("Booking paid successfully");
        return ResponseEntity.ok().build();
    }
}
