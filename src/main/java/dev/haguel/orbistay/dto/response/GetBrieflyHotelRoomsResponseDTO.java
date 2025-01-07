package dev.haguel.orbistay.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetBrieflyHotelRoomsResponseDTO", description = "Get briefly data for hotel rooms data transfer object")
public class GetBrieflyHotelRoomsResponseDTO {
    @Schema(description = "The hotel room's ID", example = "1")
    private Long id;

    @Schema(description = "The hotel room's name", example = "Royal Respite")
    private String name;

    @Schema(description = "The hotel room's people capacity", example = "2")
    private int peopleCount;

    @Schema(description = "The hotel room's cost per night", example = "22")
    private double costPerNight;
}
