package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "JwtResponseDTO", description = "Data Transfer Object for a JWT response")
public class JwtResponseDTO {
    @Schema(description = "The type of the token", example = "Bearer")
    private final String type = "Bearer";

    @Schema(description = "The JWT access token", example = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjEsImVtYWlsIjoidGVzdEBnbWFpbC5jb20iLCJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImlhdCI6MTczMzMwNjYyMCwiZXhwIjoxNzMzNDUwNjIwfQ.T6XUz8LjBJbdLAWH8IEJsnn2ayNFh6y3eVAKUMeq0Dw")
    private String accessToken;

    @Schema(description = "The JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjEsImVtYWlsIjoidGVzdEBnbWFpbC5jb20iLCJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImlhdCI6MTczMzMwNjYyMCwiZXhwIjoxNzMzNDUwNjIwfQ.T6XUz8LjBJbdLAWH8IEJsnn2ayNFh6y3eVAKUMeq0Dw")
    private String refreshToken;
}
