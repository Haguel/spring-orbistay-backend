package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.dto.request.enumeration.HotelStars;
import dev.haguel.orbistay.dto.request.enumeration.ObjectValuation;
import dev.haguel.orbistay.entity.HotelHighlight;
import dev.haguel.orbistay.entity.HotelRoom;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetFilteredHotelsResponseDTO", description = "Get hotels response data transfer object")
public class GetFilteredHotelsResponseDTO {
    @ArraySchema(schema = @Schema(implementation = FilteredHotelDTO.class))
    @Valid
    private List<FilteredHotelDTO> hotels;

    @Schema(description = "Hotels count by valuations")
    @ArraySchema(schema = @Schema(implementation = HotelsCountByValuations.class))
    private List<HotelsCountByValuations> hotelsCountByValuations;

    @Schema(description = "Hotels count by stars")
    @ArraySchema(schema = @Schema(implementation = HotelsCountByStars.class))
    private List<HotelsCountByStars> hotelsCountByStars;

    @RequiredArgsConstructor
    public static class HotelsCountByValuations {
        public final ObjectValuation objectValuation;
        public final int count;
    }

    @RequiredArgsConstructor
    public static class HotelsCountByStars {
        public final HotelStars hotelStars;
        public final int count;
    }
}
