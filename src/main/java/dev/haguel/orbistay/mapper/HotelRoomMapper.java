package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.response.GetBrieflyHotelRoomsResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class HotelRoomMapper {
    @Mapping(target = "peopleCount", source = "capacity")
    public abstract GetBrieflyHotelRoomsResponseDTO hotelRoomToBrieflyHotelRoomsDTO(HotelRoom hotelRoom);
}
