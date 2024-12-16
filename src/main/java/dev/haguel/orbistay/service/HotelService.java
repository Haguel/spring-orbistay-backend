package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.GetHotelsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.HotelsNotFoundException;
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

    public List<GetHotelsResponseDTO> getHotels(GetHotelsRequestDTO getHotelsRequestDTO) throws HotelsNotFoundException {
        List<Hotel> hotels = hotelRepository.findHotels(getHotelsRequestDTO.getName(),
                getHotelsRequestDTO.getCity(),
                getHotelsRequestDTO.getCountry(),
                getHotelsRequestDTO.getPeopleCount(),
                getHotelsRequestDTO.getIsChildrenFriendly(),
                getHotelsRequestDTO.getCheckIn(),
                getHotelsRequestDTO.getCheckOut()).orElse(null);

        if (hotels == null || hotels.isEmpty()) {
            throw new HotelsNotFoundException("No hotels found for given criteria");
        }

        log.info("Found {} hotels", hotels.size());
        hotels = hotels.subList(0, Math.min(hotels.size(), 50));

        List<GetHotelsResponseDTO> hotelsResponses = hotels.stream()
                .map(hotel -> GetHotelsResponseDTO.builder()
                        .id(hotel.getId())
                        .name(hotel.getName())
                        .shortDesc(hotel.getShortDesc())
                        .fullDesc(hotel.getFullDesc())
                        .stars(hotel.getStars())
                        .mainImageUrl(hotel.getMainImageUrl())
                        .hotelHighlights(hotel.getHotelHighlights())
                        .reviewsCount(hotel.getReviews().size())
                        .avgRate(hotel.getReviews().stream().mapToDouble(review -> review.getRate()).average().orElse(0))
                        .build())
                .collect(Collectors.toList());

        log.info("Returning {} hotels", hotelsResponses.size());
        return hotelsResponses;
    }
}
