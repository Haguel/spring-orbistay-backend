package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsIncludeRoomResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.repository.HotelRepository;
import dev.haguel.orbistay.util.mapper.HotelMapperUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class HotelMapperTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelMapperUtil hotelMapperUtil;

    @Autowired
    private HotelMapper hotelMapper;

    @Test
    @Transactional
    void whenHotelToHotelsIncludeRoomResponseDTO_thenReturnGetHotelsIncludeRoomResponseDTO() {
        Hotel hotel = hotelRepository.findById(1L).orElse(null);
        GetHotelsIncludeRoomResponseDTO responseDTO = hotelMapper.hotelToHotelsIncludeRoomResponseDTO(hotel);

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

    @Test
    @Transactional
    void whenHotelToHotelIncludeRoomResponseDTO_thenReturnGetHotelResponseDTO() {
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

    @Test
    void whenHotelIsNull_thenReturnNull() {
        GetHotelsIncludeRoomResponseDTO hotelsResponseDTO = hotelMapper.hotelToHotelsIncludeRoomResponseDTO(null);
        GetHotelResponseDTO hotelResponseDTO = hotelMapper.hotelToHotelResponseDTO(null);

        assertNull(hotelsResponseDTO);
        assertNull(hotelResponseDTO);
    }
}