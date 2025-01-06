package dev.haguel.orbistay.dto;

import dev.haguel.orbistay.annotation.ValidDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "PassportDataRequestDTO", description = "Data Transfer Object for passport data")
public class PassportDataRequestDTO {
    @Schema(description = "First name of the user", example = "John")
    @NotNull
    @NotBlank
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    @NotNull
    @NotBlank
    private String lastName;

    @Schema(description = "Passport number of the user", example = "123456789")
    @NotNull
    @NotBlank
    @Pattern(regexp="(^(?!^0+$)[a-zA-Z0-9]{3,20}$)", message = "Invalid passport number")
    private String passportNumber;

    @Schema(description = "Id of the country of issuance", example = "1")
    @NotNull
    @NotBlank
    private String countryOfIssuanceId;

    @Schema(description = "Date of issuance of the passport", example = "2024-01-20")
    @NotNull
    @NotBlank
    @ValidDate
    private String expirationDate;
}
