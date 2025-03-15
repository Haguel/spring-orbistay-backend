package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BookingInfoResponseDTO", description = "Data Transfer Object for getting booking info")
public class BookingInfoResponseDTO {
    @Schema(description = "Booking ID", example = "1")
    private Long id;

    @Schema(description = "Check in date", example = "2021-12-01T12:00:00")
    private LocalDateTime checkIn;

    @Schema(description = "Check out date", example = "2021-12-03T12:00:00")
    private LocalDateTime checkOut;

    @Schema(description = "Guest first name", example = "John")
    private String firstName;

    @Schema(description = "Guest last name", example = "Doe")
    private String lastName;

    @Schema(description = "Guest email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Guest phone number", example = "1234567890")
    private String phone;

    @Schema(description = "Guest country", implementation = Country.class)
    private Country country;

    @Schema(description = "Hotel room id", example = "1")
    private Long hotelRoomId;

    @Schema(description = "Hotel id", example = "1")
    private Long hotelId;

    @Schema(description = "Booking status", implementation = BookingStatus.class)
    private BookingStatus status;

    @Schema(description = "Payment info", implementation = Payment.class)
    private Payment payment;

}
