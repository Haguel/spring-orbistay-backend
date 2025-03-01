package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.HotelRoom;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoomRepositoryTest extends BaseRepositoryTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private RoomRepository roomRepository;

    @Nested
    class FindHotelRooms {
        @Test
        void whenFindHotelRoomsByHotelId_thenReturnHotelRooms() {
            Long hotelId = 1L;
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, null, null, null, null, null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.stream().allMatch(room -> room.getHotel().getId().equals(hotelId)));
        }

        @Test
        void whenFindHotelRoomsByPeopleCount_thenReturnHotelRooms() {
            Long hotelId = 1L;
            int peopleCount = 2;
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, peopleCount, null, null, null, null, null)
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
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, null, null, null, null, minPrice, maxPrice)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.stream().allMatch(room -> room.getCostPerNight() >= minPrice && room.getCostPerNight() <= maxPrice));
        }

        @Test
        void whenFindHotelRoomsByAvailability_thenReturnHotelRooms() {
            Long hotelId = 1L;
            LocalDate checkIn = LocalDate.of(2025, 1, 1);
            LocalDate checkOut = LocalDate.of(2025, 1, 10);
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, null, null, checkIn.atStartOfDay(), checkOut.atStartOfDay(), null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomsByInvalidHotelId_thenReturnEmpty() {
            Long invalidHotelId = 999L;
            List<HotelRoom> result = roomRepository.findHotelRooms(invalidHotelId, null, null, null, null, null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomsByInvalidPeopleCount_thenReturnEmpty() {
            Long hotelId = 2L;
            int invalidPeopleCount = 10;
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, invalidPeopleCount, null, null, null, null, null).orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomsByInvalidPriceRange_thenReturnEmpty() {
            Long hotelId = 1L;
            double minPrice = 1000.0;
            double maxPrice = 2000.0;
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, null, null, null, null, minPrice, maxPrice)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomsByInvalidAvailability_thenReturnEmpty() {
            Long hotelId = 1L;
            LocalDate checkIn = LocalDate.of(2025, 12, 1);
            LocalDate checkOut = LocalDate.of(2025, 12, 10);
            List<HotelRoom> result = roomRepository.findHotelRooms(hotelId, null, null, checkIn.atStartOfDay(), checkOut.atStartOfDay(), null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class FindHotelRoom {
        @Test
        void whenFindHotelRoomByHotelId_thenReturnHotelRoom() {
            Long hotelId = 1L;
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, null, null, null, null, null, null);

            assertTrue(result.isPresent());
            assertEquals(hotelId, result.get().getHotel().getId());
        }

        @Test
        void whenFindHotelRoomByPeopleCount_thenReturnHotelRoom() {
            Long hotelId = 1L;
            int peopleCount = 2;
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, peopleCount, null, null, null, null, null);

            assertTrue(result.isPresent());
            assertTrue(result.get().getCapacity() >= peopleCount);
        }

        @Test
        void whenFindHotelRoomByPriceRange_thenReturnHotelRoom() {
            Long hotelId = 1L;
            double minPrice = 5.0;
            double maxPrice = 50.0;
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, null, null, null, null, minPrice, maxPrice);

            assertTrue(result.isPresent());
            assertTrue(result.get().getCostPerNight() >= minPrice && result.get().getCostPerNight() <= maxPrice);
        }

        @Test
        void whenFindHotelRoomByAvailability_thenReturnHotelRoom() {
            Long hotelId = 1L;
            LocalDate checkIn = LocalDate.of(2024, 1, 1);
            LocalDate checkOut = LocalDate.of(2024, 1, 10);
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, null, null, checkIn, checkOut, null, null);

            assertTrue(result.isPresent());
        }

        @Test
        void whenFindHotelRoomByInvalidHotelId_thenReturnEmpty() {
            Long invalidHotelId = 999L;
            Optional<HotelRoom> result = roomRepository.findHotelRoom(invalidHotelId, null, null, null, null, null, null);

            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomByInvalidPeopleCount_thenReturnEmpty() {
            Long hotelId = 2L;
            int invalidPeopleCount = 10;
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, invalidPeopleCount, null, null, null, null, null);

            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomByInvalidPriceRange_thenReturnEmpty() {
            Long hotelId = 1L;
            double minPrice = 1000.0;
            double maxPrice = 2000.0;
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, null, null, null, null, minPrice, maxPrice);

            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelRoomByInvalidAvailability_thenReturnEmpty() {
            Long hotelId = 1L;
            LocalDate checkIn = LocalDate.of(2025, 12, 1);
            LocalDate checkOut = LocalDate.of(2025, 12, 10);
            Optional<HotelRoom> result = roomRepository.findHotelRoom(hotelId, null, null, checkIn, checkOut, null, null);

            assertTrue(result.isEmpty());
        }
    }
}