package dev.haguel.orbistay.dto.request;

import dev.haguel.orbistay.dto.request.enumeration.HotelStars;
import dev.haguel.orbistay.dto.request.enumeration.ObjectValuation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "HotelFiltersDTO", description = "Hotel filters data transfer object")
public class HotelFiltersDTO {
    @Schema(description = "The minimum price of the hotel room per night", example = "5.0")
    @PositiveOrZero
    private String minPrice;

    @Schema(description = "The maximum price of hotel room per night", example = "25.0")
    @PositiveOrZero
    private String maxPrice;

    @ArraySchema(arraySchema = @Schema(description = "The valuations of the hotel", implementation = ObjectValuation.class))
    private List<ObjectValuation> valuations;

    @ArraySchema(arraySchema = @Schema(description = "The stars of the hotel", implementation = HotelStars.class))
    private List<HotelStars> stars;
}
