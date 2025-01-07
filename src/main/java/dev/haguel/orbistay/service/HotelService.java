package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.exception.HotelsNotFoundException;
import dev.haguel.orbistay.mapper.HotelMapper;
import dev.haguel.orbistay.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {
    public final HotelRepository hotelRepository;
    public final HotelMapper hotelMapper;

    @Transactional(readOnly = true)
    public List<GetHotelsResponseDTO> getHotels(GetHotelsRequestDTO getHotelsRequestDTO)
            throws HotelsNotFoundException {
        Integer peopleCount = Optional.ofNullable(getHotelsRequestDTO.getPeopleCount()).map(Integer::parseInt).orElse(null);
        Boolean isChildrenFriendly = Optional.ofNullable(getHotelsRequestDTO.getIsChildrenFriendly()).map(Boolean::parseBoolean).orElse(null);
        LocalDate checkIn = Optional.ofNullable(getHotelsRequestDTO.getCheckIn()).map(LocalDate::parse).orElse(null);
        LocalDate checkOut = Optional.ofNullable(getHotelsRequestDTO.getCheckOut()).map(LocalDate::parse).orElse(null);
        Double minPrice = Optional.ofNullable(getHotelsRequestDTO.getMinPrice()).map(Double::parseDouble).orElse(null);
        Double maxPrice = Optional.ofNullable(getHotelsRequestDTO.getMaxPrice()).map(Double::parseDouble).orElse(null);
        Integer minRating = Optional.ofNullable(getHotelsRequestDTO.getMinRating()).map(Integer::parseInt).orElse(null);
        Integer maxRating = Optional.ofNullable(getHotelsRequestDTO.getMaxRating()).map(Integer::parseInt).orElse(null);
        Integer minStars = Optional.ofNullable(getHotelsRequestDTO.getMinStars()).map(Integer::parseInt).orElse(null);
        Integer maxStars = Optional.ofNullable(getHotelsRequestDTO.getMaxStars()).map(Integer::parseInt).orElse(null);

        List<Hotel> hotels = hotelRepository.findHotels(
                getHotelsRequestDTO.getName(),
                getHotelsRequestDTO.getCity(),
                getHotelsRequestDTO.getCountry(),
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

        List<GetHotelsResponseDTO> hotelsResponses = hotels.stream()
                .map(hotelMapper::hotelToHotelsResponseDTO)
                .collect(Collectors.toList());

        log.info("Returning {} hotels", hotelsResponses.size());
        return hotelsResponses;
    }

    @Transactional(readOnly = true)
    public GetHotelResponseDTO getHotelById(Long id)
            throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(id).orElse(null);

        if(hotel == null) {
            log.error("No hotel found with the given ID");
            throw new HotelNotFoundException("No hotel found with the given ID");
        }

        GetHotelResponseDTO getHotelResponseDTO = hotelMapper.hotelToHotelResponseDTO(hotel);

        return getHotelResponseDTO;
    }
}
