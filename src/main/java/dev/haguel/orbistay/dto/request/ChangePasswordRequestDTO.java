package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ChangePasswordRequestDTO", description = "Data Transfer Object for changing user's password")
public class ChangePasswordRequestDTO {
    @Schema(description = "The old password of the user", example = "oldPassword123")
    @NotNull
    @NotBlank
    private String oldPassword;

    @Schema(description = "The new password of the user", example = "newPassword123")
    @NotNull
    @NotBlank
    private String newPassword;
}
