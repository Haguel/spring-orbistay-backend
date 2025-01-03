package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.HotelRoom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class HotelRoomRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Test
    void whenFindHotelRoomsByHotelId_thenReturnHotelRooms() {
        Long hotelId = 1L;
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, null, null, null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(room -> room.getHotel().getId().equals(hotelId)));
    }

    @Test
    void whenFindHotelRoomsByPeopleCount_thenReturnHotelRooms() {
        Long hotelId = 1L;
        int peopleCount = 2;
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, peopleCount, null, null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(room -> room.getCapacity() >= peopleCount));
    }

    @Test
    void whenFindHotelRoomsByPriceRange_thenReturnHotelRooms() {
        Long hotelId = 1L;
        double minPrice = 5.0;
        double maxPrice = 50.0;
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, null, null, null, null, minPrice, maxPrice)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(room -> room.getCostPerNight() >= minPrice && room.getCostPerNight() <= maxPrice));
    }

    @Test
    void whenFindHotelRoomsByAvailability_thenReturnHotelRooms() {
        Long hotelId = 1L;
        LocalDate checkIn = LocalDate.of(2024, 1, 1);
        LocalDate checkOut = LocalDate.of(2024, 1, 10);
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, null, null, checkIn, checkOut, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whenFindHotelRoomsByInvalidHotelId_thenReturnEmpty() {
        Long invalidHotelId = 999L;
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(invalidHotelId, null, null, null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelRoomsByInvalidPeopleCount_thenReturnEmpty() {
        Long hotelId = 2L;
        int invalidPeopleCount = 10;
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, invalidPeopleCount, null, null, null, null, null).orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelRoomsByInvalidPriceRange_thenReturnEmpty() {
        Long hotelId = 1L;
        double minPrice = 1000.0;
        double maxPrice = 2000.0;
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, null, null, null, null, minPrice, maxPrice)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelRoomsByInvalidAvailability_thenReturnEmpty() {
        Long hotelId = 1L;
        LocalDate checkIn = LocalDate.of(2024, 12, 1);
        LocalDate checkOut = LocalDate.of(2024, 12, 10);
        List<HotelRoom> result = hotelRoomRepository.findHotelRooms(hotelId, null, null, checkIn, checkOut, null, null)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}