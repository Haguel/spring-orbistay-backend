package dev.haguel.orbistay.dto.request;

import dev.haguel.orbistay.annotation.ValidBoolean;
import dev.haguel.orbistay.annotation.ValidDateFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetFilteredHotelsRequestDTO", description = "Get filtered hotels request data transfer object")
public class GetFilteredHotelsRequestDTO {
    @Schema(description = "The name of the hotel", example = "Royal Respite")
    private String name;

    @Schema(description = "The city where the hotel is located", example = "New York")
    private String city;

    @Schema(description = "The country's id where the hotel is located", example = "1")
    private String countryId;

    @Schema(description = "The count of people the hotel's rooms can accommodate", example = "2")
    @PositiveOrZero
    private String peopleCount;

    @Schema(description = "Whether the hotel is children-friendly", example = "true")
    @ValidBoolean
    private String isChildrenFriendly;

    @Schema(description = "Check-in date", example = "2022-12-01")
    @ValidDateFormat
    private String checkIn;

    @Schema(description = "Check-out date", example = "2022-12-10")
    @ValidDateFormat
    private String checkOut;

    @Schema(description = "The filters to apply to the hotels")
    @Valid
    private HotelFiltersDTO filters;
}
