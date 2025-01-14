package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.entity.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetPopularDestinationsResponseDTO", description = "Get popular destinations response data transfer object")
public class GetPopularDestinationsResponseDTO {
    @Schema(description = "The destination's country info", implementation = Country.class)
    public Country country;

    @Schema(description = "The destination's city", example = "New York")
    public String city;
}
