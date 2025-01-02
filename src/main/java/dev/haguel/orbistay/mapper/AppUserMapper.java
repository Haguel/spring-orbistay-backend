package dev.haguel.orbistay.mapper;
import dev.haguel.orbistay.dto.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import org.mapstruct.*;
import org.mapstruct.factory.*;

@Mapper
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    GetAppUserInfoResponseDTO appUserToAppUserInfoDTO(AppUser appUser);
}
