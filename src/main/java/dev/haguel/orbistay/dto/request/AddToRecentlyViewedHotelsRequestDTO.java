package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Add to recently viewed hotels request")
public class AddToRecentlyViewedHotelsRequestDTO {
    @Schema(description = "Hotel ID", example = "1")
    @NotNull
    @NotBlank
    private String hotelId;
}
