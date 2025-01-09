package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "WriteReviewRequestDTO", description = "Data Transfer Object for writing a review")
public class WriteReviewRequestDTO {
    @Schema(description = "Id of the hotel to write a review for", example = "1")
    @NotNull
    @NotBlank
    private String hotelId;

    @Schema(description = "Content of the review", example = "This hotel was great!")
    @Size(min = 10)
    private String content;

    @Schema(description = "Rate of the hotel", example = "8")
    @Pattern(regexp = "^(10(\\.0{1,2})?|[1-9](\\.\\d{1,2})?)$", message = "Rate must be between 1 and 10, with up to two decimal places")
    @NotNull
    @NotBlank
    private String rate;

    @Schema(description = "What was good about the hotel", example = "The staff was very friendly")
    private String goodSides;

    @Schema(description = "What was bad about the hotel", example = "The room was a bit small")
    private String badSides;
}
