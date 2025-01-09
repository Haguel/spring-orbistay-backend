package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.response.GetBrieflyHotelRoomsResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.repository.HotelRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
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
class HotelRoomMapperTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private HotelRoomMapper hotelRoomMapper;

    @Nested
    class GetBrieflyHotelRoomsResponseDTOMapping {
        @Test
        void whenHotelRoomToBrieflyHotelRoomResponseDTO_thenReturnBrieflyHotelRoomResponseDTOO() {
            HotelRoom hotelRoom = hotelRoomRepository.findById(1L).orElse(null);
            GetBrieflyHotelRoomsResponseDTO responseDTO = hotelRoomMapper.hotelRoomToBrieflyHotelRoomsDTO(hotelRoom);

            assertNotNull(responseDTO);
            assertEquals(hotelRoom.getId(), responseDTO.getId());
            assertEquals(hotelRoom.getName(), responseDTO.getName());
            assertEquals(hotelRoom.getCostPerNight(), responseDTO.getCostPerNight());
            assertEquals(hotelRoom.getCapacity(), responseDTO.getPeopleCount());
        }

        @Test
        void whenHotelRoomIsNull_thenReturnNull() {
            GetBrieflyHotelRoomsResponseDTO responseDTO = hotelRoomMapper.hotelRoomToBrieflyHotelRoomsDTO(null);

            assertNull(responseDTO);
        }
    }
}