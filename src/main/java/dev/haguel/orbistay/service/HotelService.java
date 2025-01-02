package dev.haguel.orbistay.service;

import com.google.common.collect.Lists;
import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.HotelNotFoundException;
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

    public GetHotelResponseDTO getHotelById(Long id) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(id).orElse(null);

        if(hotel == null) {
            log.error("No hotel found with the given ID");
            throw new HotelNotFoundException("No hotel found with the given ID");
        }

        String address = hotel.getAddress().toString();
        double reviewsAvgRate = hotel.getReviews().stream().mapToDouble(review -> review.getRate()).average().orElse(0);

        List<String> imagesUrls = Lists.newArrayList(hotel.getMainImageUrl());
        imagesUrls.addAll(hotel.getHotelRooms()
                .stream()
                .flatMap(hotelRoom -> hotelRoom.getImagesUrls().stream())
                .limit(19)
                .collect(Collectors.toList()));

        List<GetBrieflyHotelRoomsResponseDTO> getBrieflyHotelRoomsResponseDTOs = hotel.getHotelRooms()
                .stream()
                .map(hotelRoom -> GetBrieflyHotelRoomsResponseDTO.builder()
                        .id(hotelRoom.getId())
                        .name(hotelRoom.getName())
                        .peopleCount(hotelRoom.getCapacity())
                        .costPerNight(hotelRoom.getCostPerDay())
                        .build())
                .collect(Collectors.toList());

        GetHotelResponseDTO getHotelResponseDTO = GetHotelResponseDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .shortDesc(hotel.getShortDesc())
                .fullDesc(hotel.getFullDesc())
                .address(address)
                .stars(hotel.getStars())
                .imagesUrls(imagesUrls)
                .hotelHighlights(hotel.getHotelHighlights())
                .reviewsCount(hotel.getReviews().size())
                .avgRate(reviewsAvgRate)
                .reviews(hotel.getReviews())
                .hotelRooms(getBrieflyHotelRoomsResponseDTOs)
                .build();

        return getHotelResponseDTO;
    }
}
