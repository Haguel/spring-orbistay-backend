package dev.haguel.orbistay.controller;

import dev.haguel.orbistay.dto.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.GetHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class HotelControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenGetHotelsWithValidCriteria_thenReturnHotels() {
        GetHotelsRequestDTO requestDTO = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("United States")
                .peopleCount(String.valueOf(2))
                .isChildrenFriendly(String.valueOf(true))
                .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                .minPrice(String.valueOf(5.0))
                .maxPrice(String.valueOf(25.0))
                .minRating(String.valueOf(3))
                .maxRating(String.valueOf(5))
                .minStars(String.valueOf(3))
                .maxStars(String.valueOf(5))
                .build();

        webTestClient.mutate()
                .build()
                .method(HttpMethod.GET)
                .uri("/hotel/get/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GetHotelsResponseDTO.class)
                .value(hotels -> {
                    assertNotNull(hotels);
                    assertFalse(hotels.isEmpty());
                });
    }

    @Test
    void whenGetHotelsWithInvalidCriteria_thenReturnError() {
        GetHotelsRequestDTO requestDTO = GetHotelsRequestDTO.builder()
                .name("Invalid Hotel")
                .city("Invalid City")
                .country("Invalid Country")
                .build();

        webTestClient.mutate()
                .build()
                .method(HttpMethod.GET)
                .uri("/hotel/get/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetHotelWithValidId_thenReturnHotel() {
        Long validHotelId = 1L;

        webTestClient.mutate()
                .build()
                .get()
                .uri("/hotel/get/" + validHotelId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetHotelResponseDTO.class)
                .value(hotel -> {
                    assertNotNull(hotel);
                    assertEquals(validHotelId, hotel.getId());
                });
    }

    @Test
    void whenGetHotelWithInvalidId_thenReturnError() {
        Long invalidHotelId = 999L;

        webTestClient.mutate()
                .build()
                .get()
                .uri("/hotel/get/" + invalidHotelId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetHotelRoomsWithValidCriteria_thenReturnHotelRooms() {
        GetHotelRoomsRequestDTO requestDTO = GetHotelRoomsRequestDTO.builder()
                .hotelId(String.valueOf(1L))
                .peopleCount(String.valueOf(2))
                .isChildrenFriendly(String.valueOf(true))
                .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                .minPrice(String.valueOf(5.0))
                .maxPrice(String.valueOf(25.0))
                .build();

        webTestClient.mutate()
                .build()
                .method(HttpMethod.GET)
                .uri("/hotel/room/get/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(HotelRoom.class)
                .value(hotelRooms -> {
                    assertNotNull(hotelRooms);
                    assertFalse(hotelRooms.isEmpty());
                });
    }

    @Test
    void whenGetHotelRoomsWithInvalidCriteria_thenReturnError() {
        GetHotelRoomsRequestDTO requestDTO = GetHotelRoomsRequestDTO.builder()
                .hotelId(String.valueOf(999L))
                .peopleCount(String.valueOf(2))
                .isChildrenFriendly(String.valueOf(true))
                .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                .minPrice(String.valueOf(5.0))
                .maxPrice(String.valueOf(25.0))
                .build();

        webTestClient.mutate()
                .build()
                .method(HttpMethod.GET)
                .uri("/hotel/room/get/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetHotelRoomWithValidId_thenReturnHotelRoom() {
        Long validHotelRoomId = 1L;

        webTestClient.mutate()
                .build()
                .get()
                .uri("/hotel/room/get/" + validHotelRoomId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(HotelRoom.class)
                .value(hotelRoom -> {
                    assertNotNull(hotelRoom);
                    assertEquals(validHotelRoomId, hotelRoom.getId());
                });
    }

    @Test
    void whenGetHotelRoomWithInvalidId_thenReturnError() {
        Long invalidHotelRoomId = 999L;

        webTestClient.mutate()
                .build()
                .get()
                .uri("/hotel/room/get/" + invalidHotelRoomId)
                .exchange()
                .expectStatus().isNotFound();
    }
}