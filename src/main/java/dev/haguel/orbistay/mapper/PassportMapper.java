package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.PassportDataRequestDTO;
import dev.haguel.orbistay.entity.Passport;
import dev.haguel.orbistay.util.mapper.SharedMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PassportMapper {
    @Autowired
    protected SharedMapperUtil sharedMapperUtil;

    @Mapping(target = "issuingCountry", expression = "java(sharedMapperUtil.getCountryById(passportDataRequestDTO.getCountryOfIssuanceId()))")
    public abstract Passport passportDataRequestDTOToPassport(PassportDataRequestDTO passportDataRequestDTO);

}
