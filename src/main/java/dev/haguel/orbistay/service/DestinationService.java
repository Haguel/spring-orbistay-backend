package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.response.GetPopularDestinationsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.mapper.HotelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DestinationService {
    private final HotelService hotelService;
    private final HotelMapper hotelMapper;

    public List<GetPopularDestinationsResponseDTO> getPopularDestinations() {
        List<Hotel> hotels = hotelService.getPopularHotelsRaw();

        List<GetPopularDestinationsResponseDTO> popularDestinations = hotels.stream()
                .map(hotelMapper::hotelToPopularDestinationsResponseDTO)
                .toList();

        log.info("Find {} popular destinations", popularDestinations.size());
        return popularDestinations;
    }
}
