package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank
    private String username;

    @Email
    private String email;

    @Pattern(regexp="(^$|[0-9]{10})", message = "Invalid phone number")
    private String phone;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format, expected yyyy-mm-dd")
    private String birthDate;

    @NotBlank
    private String gender;

    @NotBlank
    private String citizenshipCountryId;

    @Valid
    private AddressDataRequestDTO address;

    @Valid
    private PassportDataRequestDTO passport;
}
