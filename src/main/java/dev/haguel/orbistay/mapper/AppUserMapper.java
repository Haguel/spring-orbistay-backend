package dev.haguel.orbistay.mapper;
import dev.haguel.orbistay.dto.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class AppUserMapper {
    @Mapping(target = "birthDate", source = "birthDate")
    public abstract GetAppUserInfoResponseDTO appUserToAppUserInfoDTO(AppUser appUser);
}
