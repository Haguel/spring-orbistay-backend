package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.response.GetDestinationsResponseDTO;
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

    public List<GetDestinationsResponseDTO> getPopularDestinations() {
        List<Hotel> hotels = hotelService.getPopularHotelsRaw();

        List<GetDestinationsResponseDTO> popularDestinations = hotels.stream()
                .map(hotelMapper::hotelToPopularDestinationsResponseDTO)
                .toList();

        log.info("Found {} popular destinations", popularDestinations.size());
        return popularDestinations;
    }

    public List<GetDestinationsResponseDTO> getDestinationsSimilarToText(String text) {
        List<Hotel> hotels = hotelService.getHotelsWithAddressSimilarToText(text);

        List<GetDestinationsResponseDTO> similarDestinations = hotels.stream()
                .map(hotelMapper::hotelToPopularDestinationsResponseDTO)
                .toList();

        log.info("Found {} similar destinations to '{}'", similarDestinations.size(), text);
        return similarDestinations;
    }
}
