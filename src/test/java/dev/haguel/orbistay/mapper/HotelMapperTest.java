package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.response.*;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.repository.HotelRepository;
import dev.haguel.orbistay.util.mapper.HotelMapperUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class HotelMapperTest extends BaseMapperTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelMapperUtil hotelMapperUtil;

    @Autowired
    private HotelMapper hotelMapper;

    @Nested
    class FilteredHotelsDTOMapping {
        @Test
        void whenHotelToHotelsIncludeRoomResponseDTO_thenReturnGetHotelsIncludeRoomResponseDTO() {
            Hotel hotel = hotelRepository.findById(1L).orElse(null);
            FilteredHotelDTO responseDTO = hotelMapper.hotelToFilteredHotelDTO(hotel);

            assertNotNull(responseDTO);
            assertEquals(hotel.getId(), responseDTO.getId());
            assertEquals(hotel.getName(), responseDTO.getName());
            assertEquals(hotel.getShortDesc(), responseDTO.getShortDesc());
            assertEquals(hotel.getFullDesc(), responseDTO.getFullDesc());
            assertEquals(hotel.getStars(), responseDTO.getStars());
            assertEquals(hotel.getMainImageUrl(), responseDTO.getMainImageUrl());
            assertEquals(hotel.getHotelHighlights().size(), responseDTO.getHotelHighlights().size());
            assertEquals(hotel.getReviews().size(), responseDTO.getReviewsCount());
            assertEquals(hotelMapperUtil.getReviewsAvgRate(hotel), responseDTO.getAvgRate());
        }
    }

    @Nested
    class GetHotelResponseDTOMapping {
        @Test
        void whenHotelToHotelResponseDTO_thenReturnGetHotelResponseDTO() {
            Hotel hotel = hotelRepository.findById(1L).orElse(null);
            GetHotelResponseDTO responseDTO = hotelMapper.hotelToHotelResponseDTO(hotel);

            assertNotNull(responseDTO);
            assertEquals(hotel.getId(), responseDTO.getId());
            assertEquals(hotel.getName(), responseDTO.getName());
            assertEquals(hotel.getShortDesc(), responseDTO.getShortDesc());
            assertEquals(hotel.getFullDesc(), responseDTO.getFullDesc());
            assertEquals(hotel.getStars(), responseDTO.getStars());
            assertEquals(hotel.getHotelHighlights().size(), responseDTO.getHotelHighlights().size());
            assertEquals(hotel.getReviews().size(), responseDTO.getReviewsCount());
            assertEquals(hotelMapperUtil.getReviewsAvgRate(hotel), responseDTO.getAvgRate());
            assertEquals(hotelMapperUtil.getImagesUrls(hotel), responseDTO.getImagesUrls());
            assertEquals(hotelMapperUtil.getBrieflyHotelRoomsResponseDTOs(hotel), responseDTO.getHotelRooms());
        }
    }

    @Nested
    class GetHotelsResponseDTOMapping {
        @Test
        void whenHotelToHotelsResponseDTO_thenReturnGetHotelsResponseDTO() {
            Hotel hotel = hotelRepository.findById(1L).orElse(null);
            GetHotelsResponseDTO responseDTO = hotelMapper.hotelToHotelsResponseDTO(hotel);

            assertNotNull(responseDTO);
            assertEquals(hotel.getId(), responseDTO.getId());
            assertEquals(hotel.getName(), responseDTO.getName());
            assertEquals(hotel.getShortDesc(), responseDTO.getShortDesc());
            assertEquals(hotel.getFullDesc(), responseDTO.getFullDesc());
            assertEquals(hotel.getStars(), responseDTO.getStars());
            assertEquals(hotel.getMainImageUrl(), responseDTO.getMainImageUrl());
            assertEquals(hotel.getHotelHighlights().size(), responseDTO.getHotelHighlights().size());
            assertEquals(hotel.getReviews().size(), responseDTO.getReviewsCount());
            assertEquals(hotelMapperUtil.getReviewsAvgRate(hotel), responseDTO.getAvgRate());
        }
    }

    @Nested
    class GetPopularDestinationsResponseDTOMapping {
        @Test
        void whenHotelToPopularDestinationsResponseDTO_thenGetPopularDestinationsResponseDTO() {
            Hotel hotel = hotelRepository.findById(1L).orElse(null);
            GetPopularDestinationsResponseDTO responseDTO = hotelMapper.hotelToPopularDestinationsResponseDTO(hotel);

            assertNotNull(responseDTO);
            assertEquals(hotel.getAddress().getCountry(), responseDTO.getCountry());
            assertEquals(hotel.getAddress().getCity(), responseDTO.getCity());
        }
    }
}