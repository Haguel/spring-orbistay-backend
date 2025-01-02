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

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {
    public final HotelRepository hotelRepository;
    public final HotelMapper hotelMapper;

    public List<GetHotelsResponseDTO> getHotels(GetHotelsRequestDTO getHotelsRequestDTO) throws HotelsNotFoundException {
        List<Hotel> hotels = hotelRepository.findHotels(getHotelsRequestDTO.getName(),
                getHotelsRequestDTO.getCity(),
                getHotelsRequestDTO.getCountry(),
                getHotelsRequestDTO.getPeopleCount(),
                getHotelsRequestDTO.getIsChildrenFriendly(),
                getHotelsRequestDTO.getCheckIn(),
                getHotelsRequestDTO.getCheckOut(),
                getHotelsRequestDTO.getMinPrice(),
                getHotelsRequestDTO.getMaxPrice(),
                getHotelsRequestDTO.getMinRating(),
                getHotelsRequestDTO.getMaxRating(),
                getHotelsRequestDTO.getMinStars(),
                getHotelsRequestDTO.getMaxStars()).orElse(null);

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

    public GetHotelResponseDTO getHotelById(Long id) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(id).orElse(null);

        if(hotel == null) {
            log.error("No hotel found with the given ID");
            throw new HotelNotFoundException("No hotel found with the given ID");
        }

        GetHotelResponseDTO getHotelResponseDTO = hotelMapper.hotelToHotelResponseDTO(hotel);

        return getHotelResponseDTO;
    }
}
