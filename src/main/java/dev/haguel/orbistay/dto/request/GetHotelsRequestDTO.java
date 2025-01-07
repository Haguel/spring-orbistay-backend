package dev.haguel.orbistay.dto.request;

import dev.haguel.orbistay.annotation.ValidBoolean;
import dev.haguel.orbistay.annotation.ValidDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetHotelsRequestDTO", description = "Get hotels request data transfer object")
public class GetHotelsRequestDTO {
    @Schema(description = "The name of the hotel", example = "Royal Respite")
    private String name;

    @Schema(description = "The city where the hotel is located", example = "New York")
    private String city;

    @Schema(description = "The country where the hotel is located", example = "United States")
    private String country;

    @Schema(description = "The count of people the hotel's rooms can accommodate", example = "2")
    @PositiveOrZero
    private String peopleCount;

    @Schema(description = "Whether the hotel is children-friendly", example = "true")
    @ValidBoolean
    private String isChildrenFriendly;

    @Schema(description = "Check-in date", example = "2022-12-01")
    @ValidDate
    private String checkIn;

    @Schema(description = "Check-out date", example = "2022-12-10")
    @ValidDate
    private String checkOut;

    @Schema(description = "The minimum price of the hotel", example = "5.0")
    @PositiveOrZero
    private String minPrice;

    @Schema(description = "The maximum price of hotel room price", example = "25.0")
    @PositiveOrZero
    private String maxPrice;

    @Schema(description = "The minimum rating of hotel room price", example = "3")
    @PositiveOrZero
    private String minRating;

    @Schema(description = "The maximum rating of the hotel", example = "5")
    @PositiveOrZero
    private String maxRating;

    @Schema(description = "The minimum number of stars of the hotel", example = "3")
    @PositiveOrZero
    private String minStars;

    @Schema(description = "The maximum number of stars of the hotel", example = "5")
    @PositiveOrZero
    private String maxStars;
}
