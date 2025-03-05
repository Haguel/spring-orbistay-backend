package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.AddBankCardDTO;
import dev.haguel.orbistay.entity.BankCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class BankCardMapper {
    public abstract BankCard addBankCardDTOToBankCard(AddBankCardDTO addBankCardDTO);
}
