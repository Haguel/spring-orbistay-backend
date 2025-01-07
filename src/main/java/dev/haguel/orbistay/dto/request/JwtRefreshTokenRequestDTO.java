package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "JwtRefreshTokenDTO", description = "Data Transfer Object for a JWT request")
public class JwtRefreshTokenRequestDTO {
    @Schema(description = "The JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTczNTk4MjM3N30.ErYXGf-iEanyTBj6hLTEc_mwQF-C8cGpSEcB8AJZFN")
    @Pattern(regexp = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$", message = "Invalid JWT refresh token")
    @NotBlank
    @NotNull
    private String refreshToken;
}
