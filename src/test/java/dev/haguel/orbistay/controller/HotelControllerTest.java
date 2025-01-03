package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.GetHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @LocalServerPort
    int port;

    private final RestTemplate restTemplate;

    public HotelControllerTest() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    void whenGetHotelsWithValidCriteria_thenReturnHotels() {
        GetHotelsRequestDTO requestDTO = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .minRating(3)
                .maxRating(5)
                .minStars(3)
                .maxStars(5)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<GetHotelsRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<GetHotelsResponseDTO[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                entity, GetHotelsResponseDTO[].class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void whenGetHotelsWithInvalidCriteria_thenReturnError() {
        // Case 1: Invalid name
        GetHotelsRequestDTO requestDTO1 = GetHotelsRequestDTO.builder()
                .name("Nonexistent Hotel")
                .city("New York")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .minRating(3)
                .maxRating(5)
                .minStars(3)
                .maxStars(5)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO1), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 2: Invalid city
        GetHotelsRequestDTO requestDTO2 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("Nonexistent City")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .minRating(3)
                .maxRating(5)
                .minStars(3)
                .maxStars(5)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO2), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 3: Invalid country
        GetHotelsRequestDTO requestDTO3 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("Nonexistent Country")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .minRating(3)
                .maxRating(5)
                .minStars(3)
                .maxStars(5)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO3), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 4: Invalid people count
        GetHotelsRequestDTO requestDTO4 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("United States")
                .peopleCount(10)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .minRating(3)
                .maxRating(5)
                .minStars(3)
                .maxStars(5)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO4), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Test
    void whenGetHotelWithValidId_thenReturnHotel() {
        Long validHotelId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GetHotelResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/" + validHotelId, HttpMethod.GET,
                entity, GetHotelResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(validHotelId, response.getBody().getId());
    }

    @Test
    void whenGetHotelWithInvalidId_thenReturnError() {
        Long invalidHotelId = 999L;

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/" + invalidHotelId, HttpMethod.GET, HttpEntity.EMPTY, GetHotelResponseDTO.class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Test
    void whenGetHotelRoomsWithValidCriteria_thenReturnHotelRooms() {
        GetHotelRoomsRequestDTO requestDTO = GetHotelRoomsRequestDTO.builder()
                .hotelId(1L)
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<GetHotelRoomsRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<HotelRoom[]> response = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/room/get/filter", HttpMethod.GET,
                entity, HotelRoom[].class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void whenGetHotelRoomsWithInvalidCriteria_thenReturnError() {
        // Case 1: Invalid hotel ID
        GetHotelRoomsRequestDTO requestDTO1 = GetHotelRoomsRequestDTO.builder()
                .hotelId(999L)
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/room/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO1), HotelRoom[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 2: Invalid people count
        GetHotelRoomsRequestDTO requestDTO2 = GetHotelRoomsRequestDTO.builder()
                .hotelId(1L)
                .peopleCount(10)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .minPrice(5.0)
                .maxPrice(25.0)
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/room/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO2), HotelRoom[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Test
    void whenGetHotelRoomWithValidId_thenReturnHotelRoom() {
        Long validHotelRoomId = 1L;

        ResponseEntity<HotelRoom> response = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/room/get/" + validHotelRoomId, HttpMethod.GET,
                HttpEntity.EMPTY, HotelRoom.class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(validHotelRoomId, response.getBody().getId());
    }

    @Test
    void whenGetHotelRoomWithInvalidId_thenReturnError() {
        Long invalidHotelRoomId = 999L;

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/room/get/" + invalidHotelRoomId, HttpMethod.GET, HttpEntity.EMPTY, HotelRoom.class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }
}