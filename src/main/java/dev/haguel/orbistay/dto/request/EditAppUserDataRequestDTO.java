package dev.haguel.orbistay.dto.request;

import dev.haguel.orbistay.annotation.ValidDateFormat;
import dev.haguel.orbistay.annotation.ValidPhoneNumber;
import dev.haguel.orbistay.entity.enumeration.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "EditAppUserDataRequestDTO", description = "Data Transfer Object for editing app user's data")
public class EditAppUserDataRequestDTO {
    @Schema(description = "The app user's username", example = "JohnDoe")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    private String username;

    @Schema(description = "The app user's email", example = "test@gmail.com")
    @Email
    private String email;

    @Schema(description = "The app user's phone", example = "1234567890")
    @ValidPhoneNumber
    private String phone;

    @Schema(description = "The app user's birth date", example = "2000-01-01")
    @ValidDateFormat
    private String birthDate;

    @Schema(description = "The app user's gender", implementation = Gender.class)
    private String gender;

    @Schema(description = "The app user's citizenship country ID", example = "1")
    private String citizenshipCountryId;

    @Schema(description = "The app user's residency address data", implementation = AddressDataRequestDTO.class)
    @Valid
    private AddressDataRequestDTO address;

    @Schema(description = "The app user's passport data", implementation = PassportDataRequestDTO.class)
    @Valid
    private PassportDataRequestDTO passport;
}
