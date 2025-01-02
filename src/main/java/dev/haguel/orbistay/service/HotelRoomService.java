package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.GetHotelRoomsRequestDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.exception.HotelRoomsNotFoundException;
import dev.haguel.orbistay.repository.HotelRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelRoomService {
    private final HotelRoomRepository hotelRoomRepository;

    public List<HotelRoom> getHotelRoomsRequestDTO(GetHotelRoomsRequestDTO getHotelRoomsRequestDTO) throws HotelRoomsNotFoundException {
        List<HotelRoom> hotelRooms = hotelRoomRepository.findHotelRooms(getHotelRoomsRequestDTO.getHotelId(),
                getHotelRoomsRequestDTO.getPeopleCount(),
                getHotelRoomsRequestDTO.getIsChildrenFriendly(),
                getHotelRoomsRequestDTO.getCheckIn(),
                getHotelRoomsRequestDTO.getCheckOut(),
                getHotelRoomsRequestDTO.getMinPrice(),
                getHotelRoomsRequestDTO.getMaxPrice()).orElse(null);

        if (hotelRooms == null || hotelRooms.isEmpty()) {
            throw new HotelRoomsNotFoundException("No hotel rooms found for given criteria");
        }

        log.info("Found {} hotel rooms", hotelRooms.size());

        return hotelRooms;
    }
}
