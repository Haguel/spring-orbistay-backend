package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.exception.HotelRoomNotFoundException;
import dev.haguel.orbistay.exception.HotelRoomsNotFoundException;
import dev.haguel.orbistay.repository.HotelRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelRoomService {
    private final HotelRoomRepository hotelRoomRepository;

    @Transactional(readOnly = true)
    public List<HotelRoom> getFilteredHotelRooms(GetFileredHotelRoomsRequestDTO getFileredHotelRoomsRequestDTO)
            throws HotelRoomsNotFoundException {
        Long hotelId = Long.parseLong(getFileredHotelRoomsRequestDTO.getHotelId());
        Integer peopleCount = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getPeopleCount()).map(Integer::parseInt).orElse(null);
        Boolean isChildrenFriendly = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getIsChildrenFriendly()).map(Boolean::parseBoolean).orElse(null);
        LocalDate checkIn = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getCheckIn()).map(LocalDate::parse).orElse(null);
        LocalDate checkOut = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getCheckOut()).map(LocalDate::parse).orElse(null);
        Double minPrice = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getMinPrice()).map(Double::parseDouble).orElse(null);
        Double maxPrice = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getMaxPrice()).map(Double::parseDouble).orElse(null);

        List<HotelRoom> hotelRooms = hotelRoomRepository.findHotelRooms(
                hotelId,
                peopleCount,
                isChildrenFriendly,
                checkIn.atStartOfDay(),
                checkOut.atStartOfDay(),
                minPrice,
                maxPrice
        ).orElse(null);

        if (hotelRooms == null || hotelRooms.isEmpty()) {
            throw new HotelRoomsNotFoundException("No hotel rooms found for given criteria");
        }

        log.info("Found {} hotel rooms", hotelRooms.size());

        return hotelRooms;
    }

    @Transactional(readOnly = true)
    public HotelRoom getHotelRoom(GetFileredHotelRoomsRequestDTO getFileredHotelRoomsRequestDTO)
            throws HotelRoomNotFoundException {
        Long hotelId = Long.parseLong(getFileredHotelRoomsRequestDTO.getHotelId());
        Integer peopleCount = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getPeopleCount()).map(Integer::parseInt).orElse(null);
        Boolean isChildrenFriendly = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getIsChildrenFriendly()).map(Boolean::parseBoolean).orElse(null);
        LocalDate checkIn = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getCheckIn()).map(LocalDate::parse).orElse(null);
        LocalDate checkOut = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getCheckOut()).map(LocalDate::parse).orElse(null);
        Double minPrice = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getMinPrice()).map(Double::parseDouble).orElse(null);
        Double maxPrice = Optional.ofNullable(getFileredHotelRoomsRequestDTO.getMaxPrice()).map(Double::parseDouble).orElse(null);

        HotelRoom hotelRoom = hotelRoomRepository.findHotelRoom(
                hotelId,
                peopleCount,
                isChildrenFriendly,
                checkIn,
                checkOut,
                minPrice,
                maxPrice
        ).orElse(null);

        if (hotelRoom == null) {
            throw new HotelRoomNotFoundException("No hotel rooms found for given criteria");
        }

        log.info("Found hotel room");

        return hotelRoom;
    }

    @Transactional(readOnly = true)
    public HotelRoom findById(Long id)
            throws HotelRoomNotFoundException {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id).orElse(null);

        if(hotelRoom == null) {
            log.warn("Hotel room couldn't be found in database by provided id: {}", id);
            throw new HotelRoomNotFoundException("Hotel room with provided id not found in database");
        } else {
            log.info("Hotel room with id {} found in database", id);
        }

        return hotelRoom;
    }
}
