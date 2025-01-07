package dev.haguel.orbistay.mapper;
import dev.haguel.orbistay.dto.response.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class AppUserMapper {
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "gender", source = "gender")
    public abstract GetAppUserInfoResponseDTO appUserToAppUserInfoDTO(AppUser appUser);
}
