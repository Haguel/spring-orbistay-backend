package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(name = "RequestPasswordResetDTO", description = "Request password reset data transfer object")
public class RequestPasswordResetRequestDTO {
    @Schema(description = "The email of the user", example = "test@example.com")
    @Email
    @NotBlank
    @NotNull
    private String email;
}
