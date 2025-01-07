package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.AddressDataRequestDTO;
import dev.haguel.orbistay.entity.Address;
import dev.haguel.orbistay.util.mapper.SharedMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {
    @Autowired
    protected SharedMapperUtil sharedMapperUtil;

    @Mapping(target = "country", expression = "java(sharedMapperUtil.getCountryById(addressDataRequestDTO.getCountryId()))")
    public abstract Address addressDataRequestDTOToAddress(AddressDataRequestDTO addressDataRequestDTO);
}
