package dev.haguel.orbistay.dto;

import dev.haguel.orbistay.annotation.ValidBoolean;
import dev.haguel.orbistay.annotation.ValidDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetHotelRoomsRequestDTO", description = "Get filtered hotel rooms request data transfer object")
public class GetHotelRoomsRequestDTO {
    @Schema(description = "The ID of the hotel", example = "1")
    @NotNull
    @NotBlank
    private String hotelId;

    @Schema(description = "The count of people the hotel's rooms can accommodate", example = "2")
    @PositiveOrZero
    @NotBlank
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

    @Schema(description = "The minimum price of hotel room price", example = "5.0")
    @PositiveOrZero
    @NotBlank
    private String minPrice;

    @Schema(description = "The maximum price of hotel room price", example = "25.0")
    @PositiveOrZero
    @NotBlank
    private String maxPrice;
}
