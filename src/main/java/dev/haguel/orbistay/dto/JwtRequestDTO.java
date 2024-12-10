package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(name = "JwtRequestDTO", description = "Data Transfer Object for a JWT request")
public class JwtRequestDTO {
    @Schema(description = "The JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTczNTk4MjM3N30.ErYXGf-iEanyTBj6hLTEc_mwQF-C8cGpSEcB8AJZFN")
    private String refreshToken;
}
