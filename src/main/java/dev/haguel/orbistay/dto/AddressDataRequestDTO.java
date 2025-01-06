package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "AddressDataRequestDTO", description = "Data Transfer Object for address data")
public class AddressDataRequestDTO {
    @Schema(description = "Id of country of the address", example = "1")
    @NotNull
    @NotBlank
    private String countryId;

    @Schema(description = "Address street", example = "1234 Elm Street, Apt 56B")
    @NotNull
    @NotBlank
    private String street;

    @Schema(description = "Address city", example = "Springfield")
    @NotNull
    @NotBlank
    private String city;
}
