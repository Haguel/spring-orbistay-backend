package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private Integer peopleCount;

    @Schema(description = "Whether the hotel is children-friendly", example = "true")
    private Boolean isChildrenFriendly;

    @Schema(description = "Check-in date", example = "2022-12-01")
    private LocalDate checkIn;

    @Schema(description = "Check-out date", example = "2022-12-10")
    private LocalDate checkOut;
}
