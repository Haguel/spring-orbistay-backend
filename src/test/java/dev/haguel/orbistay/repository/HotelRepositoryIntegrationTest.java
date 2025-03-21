package dev.haguel.orbistay.repository;

import com.google.common.collect.Lists;
import dev.haguel.orbistay.entity.Hotel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HotelRepositoryIntegrationTest extends BaseRepositoryTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private HotelRepository hotelRepository;

    @Nested
    class FindFilteredHotels {
        @Test
        void whenFindHotelsByNameCriteria_thenReturnHotel() {
            String hotelCity = "Hotel New York 1";
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            hotelCity, null, null, null, null, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(hotelCity, result.get(0).getName());
        }

        @Test
        void whenFindHotelsByCountryCriteria_thenReturnHotel() {
            HashSet<String> expected = new HashSet<>(Lists.newArrayList("Hotel New York 1", "Hotel New York 2", "Hotel New York 3"));
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, 1L, null, null, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());

            HashSet<String> actual = new HashSet<>(result.stream().map(Hotel::getName).collect(Collectors.toList()));
            assertEquals(expected, actual);
        }

        @Test
        void whenFindHotelsByPeopleCountCriteria_thenReturnHotel() {
            HashSet<String> expected = new HashSet<>(Lists.newArrayList("Hotel New York 1", "Hotel New York 2", "Hotel New York 3",
                    "Hotel Zurich 1", "Hotel Zurich 2", "Hotel Zurich 3"));
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, null, 2, null, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());

            HashSet<String> actual = new HashSet<>(result.stream().map(Hotel::getName).collect(Collectors.toList()));
            assertEquals(expected, actual);
        }

        @Test
        void whenFindHotelsByChildrenFriendlyCriteria_thenReturnHotel() {
            HashSet<String> expected = new HashSet<>(Lists.newArrayList("Hotel New York 1", "Hotel New York 2", "Hotel New York 3",
                    "Hotel Zurich 1", "Hotel Zurich 2", "Hotel Zurich 3"));
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, null, null, true, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());

            HashSet<String> actual = new HashSet<>(result.stream().map(Hotel::getName).collect(Collectors.toList()));
            assertEquals(expected, actual);
        }

        @Test
        void whenFindHotelsByCheckInAndCheckOutCriteria_thenReturnHotel() {
            HashSet<String> expected = new HashSet<>(Lists.newArrayList("Hotel New York 1", "Hotel New York 2", "Hotel New York 3",
                    "Hotel Zurich 1", "Hotel Zurich 2", "Hotel Zurich 3"));
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, null, null, null,
                            LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 8),
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());

            HashSet<String> actual = new HashSet<>(result.stream().map(Hotel::getName).collect(Collectors.toList()));
            assertEquals(expected, actual);
        }

        @Test
        void whenFindHotelsByMinMaxPriceCriteria_thenReturnHotel() {
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, null, null, null, null, null,
                            5.0, 50.0)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.stream().allMatch(
                    hotel -> hotel.getHotelRooms().stream().anyMatch(room -> room.getCostPerNight() >= 5.0 && room.getCostPerNight() <= 50.0)));
        }

        @Test
        void whenFindHotelsByNonExistentName_thenReturnEmpty() {
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            "Nonexistent Hotel", null, null, null, null, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelsByNonExistentCountry_thenReturnEmpty() {
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, -1L, null, null, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelsByInvalidPeopleCount_thenReturnEmpty() {
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, null, 10, null, null, null,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHoteslByTakenCheckInAndCheckOut_thenReturnEmpty() {
            LocalDate takenCheckIn = LocalDate.of(2025, 12, 1);
            LocalDate takenCheckOut = LocalDate.of(2025, 12, 10);

            String hotelName = "Hotel New York 1";
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            hotelName, null, null, null, null, takenCheckIn, takenCheckOut,
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            result = hotelRepository.findFilteredHotels(
                            hotelName, null, null, null, null,
                            takenCheckIn.minusDays(5), takenCheckOut.minusDays(5),
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            result = hotelRepository.findFilteredHotels(
                            hotelName, null, null, null, null,
                            takenCheckIn.plusDays(5), takenCheckOut.plusDays(5),
                            null, null)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelsByInvalidMinMaxPriceCriteria_thenReturnEmpty() {
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            null, null, null, null, null, null, null,
                            1000.0, 2000.0)
                    .orElse(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void whenFindHotelsByFullData_thenReturnHotel() {
            List<Hotel> result = hotelRepository.findFilteredHotels(
                            "Hotel New York 1", "New York", 1L, 2, true, LocalDate.of(2022, 12, 5), LocalDate.of(2022, 12, 8),
                            5.0, 25.0)
                    .orElse(null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals("Hotel New York 1", result.get(0).getName());
        }
    }

    @Nested
    class FindPopularHotels {
        @Test
        void whenFindPopularHotels_thenReturnHotels() {
            List<Hotel> result = hotelRepository.findPopularHotels(LocalDate.of(2024, 11, 30));

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.get(0).getHotelRooms().get(0).getId());
        }
    }

    @Nested
    class FindHotelWithSimilarAddress {
        @Test
        void whenFindHotelsWithSimilarAddress_thenReturnHotels() {
            List<Hotel> result = hotelRepository.findHotelsWithAddressSimilarToText("New York");

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertTrue(result.stream().allMatch(hotel -> hotel.getAddress().getCity().contains("New York")));

            result = hotelRepository.findHotelsWithAddressSimilarToText("Zuri");

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertTrue(result.stream().allMatch(hotel -> hotel.getAddress().getCity().contains("Zurich")));
        }
    }
}