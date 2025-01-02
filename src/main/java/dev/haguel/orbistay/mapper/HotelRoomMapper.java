package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.GetBrieflyHotelRoomsResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class HotelRoomMapper {
    public abstract GetBrieflyHotelRoomsResponseDTO hotelRoomToBrieflyHotelRoomsDTO(HotelRoom hotelRoom);
}
