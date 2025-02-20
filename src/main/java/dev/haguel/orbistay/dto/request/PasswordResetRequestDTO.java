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
public class PasswordResetRequestDTO {
    @Schema(description = "The new password of the user", example = "qwerty123")
    @NotBlank
    @NotNull
    private String newPassword;

    @Schema(description = "The JWT token for password reset", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.\n" +
            "eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk4NDA1NjAwLCJleHAiOjE2OTg0MDkyMDB9.\n" +
            "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")
    @NotBlank
    @NotNull
        private String resetPasswordJwtToken;
}
