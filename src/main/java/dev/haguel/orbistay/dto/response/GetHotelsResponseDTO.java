package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.entity.HotelHighlight;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetHotelsResponseDTO", description = "Get hotels response data transfer object")
public class GetHotelsResponseDTO {
    @Schema(description = "The hotel's ID", example = "1")
    private Long id;

    @Schema(description = "The hotel's name", example = "Royal Respite")
    private String name;

    @Schema(description = "The hotel's short description", example = "A luxurious hotel in the heart of New York")
    private String shortDesc;

    @Schema(description = "The hotel's full description", example = "A luxurious hotel in the heart of New York, offering the best services and amenities")
    private String fullDesc;

    @Schema(description = "The hotel's star rating", example = "5")
    private Integer stars;

    @Schema(description = "The hotel's main image URL", example = "https://example.com/hotel.jpg")
    private String mainImageUrl;

    @ArraySchema(schema = @Schema(implementation = HotelHighlight.class))
    @Schema(description = "The hotel's highlights")
    private List<HotelHighlight> hotelHighlights;

    @Schema(description = "The hotel's reviews count", example = "10")
    private int reviewsCount;

    @Schema(description = "The hotel's average rate", example = "4.5")
    private double avgRate;
}
