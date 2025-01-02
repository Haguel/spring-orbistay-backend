package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetHotelRoomsRequestDTO", description = "Get filtered hotel rooms request data transfer object")
public class GetHotelRoomsRequestDTO {
    @Schema(description = "The ID of the hotel", example = "1")
    @NotNull
    @NotBlank
    private Long hotelId;

    @Schema(description = "The count of people the hotel's rooms can accommodate", example = "2")
    private Integer peopleCount;

    @Schema(description = "Whether the hotel is children-friendly", example = "true")
    private Boolean isChildrenFriendly;

    @Schema(description = "Check-in date", example = "2022-12-01")
    private LocalDate checkIn;

    @Schema(description = "Check-out date", example = "2022-12-10")
    private LocalDate checkOut;

    @Schema(description = "The minimum price of the hotel", example = "5.0")
    private Double minPrice;

    @Schema(description = "The maximum price of the hotel", example = "25.0")
    private Double maxPrice;
}
