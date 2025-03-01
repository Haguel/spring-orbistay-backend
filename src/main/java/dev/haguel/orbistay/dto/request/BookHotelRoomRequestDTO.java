package dev.haguel.orbistay.dto.request;

import dev.haguel.orbistay.annotation.IsDateAfterToday;
import dev.haguel.orbistay.annotation.ValidDateFormat;
import dev.haguel.orbistay.annotation.ValidPhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Book hotel room request")
public class BookHotelRoomRequestDTO {
    @Schema(description = "Hotel room ID", example = "1")
    private String hotelRoomId;

    @Schema(description = "Check-in date", example = "2023-10-01")
    @ValidDateFormat
    @IsDateAfterToday
    @NotNull
    @NotBlank
    private String checkIn;

    @Schema(description = "Check-out date", example = "2023-10-02")
    @ValidDateFormat
    @IsDateAfterToday
    @NotNull
    @NotBlank
    private String checkOut;

    @Schema(description = "First name", example = "John")
    @NotNull
    @NotBlank
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @NotNull
    @NotBlank
    private String lastName;

    @Schema(description = "Email", example = "john.doe@example.com")
    @NotNull
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Phone number", example = "1234567890")
    @NotNull
    @NotBlank
    @ValidPhoneNumber
    private String phone;

    @Schema(description = "Country ID", example = "1")
    @NotNull
    @NotBlank
    private String countryId;
}
