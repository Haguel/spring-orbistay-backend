package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.request.GetFilteredHotelsRequestDTO;
import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsIncludeRoomResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.exception.HotelRoomNotFoundException;
import dev.haguel.orbistay.exception.HotelsNotFoundException;
import dev.haguel.orbistay.mapper.HotelMapper;
import dev.haguel.orbistay.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final HotelRoomService hotelRoomService;

    @Transactional(readOnly = true)
    public List<GetHotelsIncludeRoomResponseDTO> getFilteredHotels(GetFilteredHotelsRequestDTO getFilteredHotelsRequestDTO)
            throws HotelsNotFoundException {
        Integer peopleCount = Optional.ofNullable(getFilteredHotelsRequestDTO.getPeopleCount()).map(Integer::parseInt).orElse(null);
        Boolean isChildrenFriendly = Optional.ofNullable(getFilteredHotelsRequestDTO.getIsChildrenFriendly()).map(Boolean::parseBoolean).orElse(null);
        LocalDate checkIn = Optional.ofNullable(getFilteredHotelsRequestDTO.getCheckIn()).map(LocalDate::parse).orElse(null);
        LocalDate checkOut = Optional.ofNullable(getFilteredHotelsRequestDTO.getCheckOut()).map(LocalDate::parse).orElse(null);
        Double minPrice = Optional.ofNullable(getFilteredHotelsRequestDTO.getMinPrice()).map(Double::parseDouble).orElse(null);
        Double maxPrice = Optional.ofNullable(getFilteredHotelsRequestDTO.getMaxPrice()).map(Double::parseDouble).orElse(null);
        Integer minRating = Optional.ofNullable(getFilteredHotelsRequestDTO.getMinRating()).map(Integer::parseInt).orElse(null);
        Integer maxRating = Optional.ofNullable(getFilteredHotelsRequestDTO.getMaxRating()).map(Integer::parseInt).orElse(null);
        Integer minStars = Optional.ofNullable(getFilteredHotelsRequestDTO.getMinStars()).map(Integer::parseInt).orElse(null);
        Integer maxStars = Optional.ofNullable(getFilteredHotelsRequestDTO.getMaxStars()).map(Integer::parseInt).orElse(null);

        List<Hotel> hotels = hotelRepository.findHotels(
                getFilteredHotelsRequestDTO.getName(),
                getFilteredHotelsRequestDTO.getCity(),
                getFilteredHotelsRequestDTO.getCountry(),
                peopleCount,
                isChildrenFriendly,
                checkIn,
                checkOut,
                minPrice,
                maxPrice,
                minRating,
                maxRating,
                minStars,
                maxStars
        ).orElse(null);

        if (hotels == null || hotels.isEmpty()) {
            throw new HotelsNotFoundException("No hotels found for given criteria");
        }

        log.info("Found {} hotels", hotels.size());
        hotels = hotels.subList(0, Math.min(hotels.size(), 50));

        List<GetHotelsIncludeRoomResponseDTO> hotelsResponses = hotels.stream()
                .map(hotelMapper::hotelToHotelsIncludeRoomResponseDTO)
                .collect(Collectors.toList());

        // Set suitable hotel room for each hotel
        for (GetHotelsIncludeRoomResponseDTO hotelResponse : hotelsResponses) {
            GetFileredHotelRoomsRequestDTO getFileredHotelRoomsRequestDTO = GetFileredHotelRoomsRequestDTO.builder()
                    .hotelId(hotelResponse.getId().toString())
                    .peopleCount(getFilteredHotelsRequestDTO.getPeopleCount())
                    .isChildrenFriendly(getFilteredHotelsRequestDTO.getIsChildrenFriendly())
                    .checkIn(getFilteredHotelsRequestDTO.getCheckIn())
                    .checkOut(getFilteredHotelsRequestDTO.getCheckOut())
                    .minPrice(getFilteredHotelsRequestDTO.getMinPrice())
                    .maxPrice(getFilteredHotelsRequestDTO.getMaxPrice())
                    .build();

            try {
                hotelResponse.setHotelRoom(hotelRoomService.getHotelRoom(getFileredHotelRoomsRequestDTO));
            } catch (HotelRoomNotFoundException e) {
                log.error("No hotel room found for the given criteria but hotel is found");
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        log.info("Returning {} hotels", hotelsResponses.size());
        return hotelsResponses;
    }

    public GetHotelResponseDTO getHotelById(Long id)
            throws HotelNotFoundException {
        Hotel hotel = findById(id);
        GetHotelResponseDTO getHotelResponseDTO = hotelMapper.hotelToHotelResponseDTO(hotel);

        return getHotelResponseDTO;
    }

    @Transactional(readOnly = true)
    public Hotel findById(Long id) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(id).orElse(null);

        if (hotel == null) {
            log.warn("Hotel with id {} not found", id);
            throw new HotelNotFoundException("Hotel not found");
        }

        return hotel;
    }

    @Transactional(readOnly = true)
    public List<GetHotelsResponseDTO> getPopularHotels() {
        List<Hotel> hotels = hotelRepository.findPopularHotels(LocalDate.now().minusDays(30));

        List<GetHotelsResponseDTO> getHotelsResponseDTOs = hotels.stream()
                .map(hotelMapper::hotelToHotelsResponseDTO)
                .collect(Collectors.toList());

        log.info("Returning {} popular hotels", getHotelsResponseDTOs.size());
        return getHotelsResponseDTOs;
    }
}
