package dev.haguel.orbistay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ChangePasswordRequestDTO", description = "Data Transfer Object for changing user's password")
public class ChangePasswordRequestDTO extends JwtAccessTokenDTO{
    @Schema(description = "The old password of the user", example = "oldPassword123")
    private String oldPassword;

    @Schema(description = "The new password of the user", example = "newPassword123")
    private String newPassword;
}
