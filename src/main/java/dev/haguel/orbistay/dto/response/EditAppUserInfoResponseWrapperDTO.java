package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.entity.AppUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EditAppUserInfoDTO", description = "Edit app user info data transfer object")
public class EditAppUserInfoResponseWrapperDTO {
    @Schema(description = "The updated app user", implementation = AppUser.class)
    EditAppUserInfoResponseDTO editAppUserInfoResponseDTO;

    @Schema(description = "The JWT response", implementation = JwtDTO.class)
    JwtDTO jwtDTO;


}
