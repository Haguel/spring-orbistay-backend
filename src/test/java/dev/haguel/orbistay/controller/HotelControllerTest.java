package dev.haguel.orbistay.controller;

import com.google.common.collect.Lists;
import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.HotelFiltersDTO;
import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.dto.request.enumeration.HotelStars;
import dev.haguel.orbistay.dto.request.enumeration.ObjectValuation;
import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.request.GetFilteredHotelsRequestDTO;
import dev.haguel.orbistay.dto.response.GetHotelsIncludeRoomResponseDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.util.EndPoints;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.SharedTestUtil;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class GetFilteredHotels {
        @Test
        void whenGetFilteredHotelsWithValidCriteria_thenReturnHotels() {
            HotelFiltersDTO hotelFiltersDTO = HotelFiltersDTO.builder()
                    .minPrice("5.0")
                    .maxPrice("25.0")
                    .stars(Lists.newArrayList(HotelStars.THREE_STARS, HotelStars.FOUR_STARS, HotelStars.FIVE_STARS))
                    .valuations(Lists.newArrayList(ObjectValuation.EXCELLENT))
                    .build();
            GetFilteredHotelsRequestDTO requestDTO = GetFilteredHotelsRequestDTO.builder()
                    .name("Hotel New York 1")
                    .city("New York")
                    .country("United States")
                    .peopleCount(String.valueOf(2))
                    .isChildrenFriendly(String.valueOf(true))
                    .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                    .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                    .filters(hotelFiltersDTO)
                    .build();

            webTestClient.mutate()
                    .build()
                    .method(HttpMethod.GET)
                    .uri(EndPoints.Hotels.GET_FILTERED_HOTELS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetHotelsIncludeRoomResponseDTO.class)
                    .value(hotels -> {
                        assertNotNull(hotels);
                        assertFalse(hotels.isEmpty());
                    });
        }

        @Test
        void whenGetFilteredHotelsWithInvalidCriteria_thenReturnError() {
            GetFilteredHotelsRequestDTO requestDTO = GetFilteredHotelsRequestDTO.builder()
                    .name("Invalid Hotel")
                    .city("Invalid City")
                    .country("Invalid Country")
                    .build();

            webTestClient.mutate()
                    .build()
                    .method(HttpMethod.GET)
                    .uri(EndPoints.Hotels.GET_FILTERED_HOTELS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetHotel {
        @Test
        void whenGetHotelWithValidId_thenReturnHotel() {
            Long validHotelId = 1L;

            webTestClient.mutate()
                    .build()
                    .get()
                    .uri(EndPoints.Hotels.GET_HOTEL + "/" + validHotelId)
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
                    .uri(EndPoints.Hotels.GET_HOTEL + "/" + invalidHotelId)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetFilteredHotelRooms {
        @Test
        void whenGetFilteredHotelRoomsWithValidCriteria_thenReturnHotelRooms() {
            GetFileredHotelRoomsRequestDTO requestDTO = GetFileredHotelRoomsRequestDTO.builder()
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
                    .uri(EndPoints.Hotels.GET_FILTERED_HOTEL_ROOMS)
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
        void whenGetFilteredHotelRoomsWithInvalidCriteria_thenReturnError() {
            GetFileredHotelRoomsRequestDTO requestDTO = GetFileredHotelRoomsRequestDTO.builder()
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
                    .uri(EndPoints.Hotels.GET_FILTERED_HOTEL_ROOMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetHotelRoom {
        @Test
        void whenGetHotelRoomWithValidId_thenReturnHotelRoom() {
            Long validHotelRoomId = 1L;

            webTestClient.mutate()
                    .build()
                    .get()
                    .uri(EndPoints.Hotels.GET_HOTEL_ROOM + "/" + validHotelRoomId)
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
                    .uri(EndPoints.Hotels.GET_HOTEL_ROOM + "/" + invalidHotelRoomId)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class WriteReview {
        @Test
        void whenWriteReviewWithValidData_thenReturnReview() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            WriteReviewRequestDTO requestDTO = WriteReviewRequestDTO.builder()
                    .hotelId(String.valueOf(1L))
                    .content("Great hotel!")
                    .rate("8.6")
                    .goodSides("The staff was very friendly")
                    .badSides("The room was a bit small")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Hotels.WRITE_REVIEW)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Review.class)
                    .value(review -> {
                        assertNotNull(review);
                        assertEquals(Long.parseLong(requestDTO.getHotelId()), review.getHotel().getId());
                        assertEquals(requestDTO.getContent(), review.getContent());
                        assertEquals(Double.parseDouble(requestDTO.getRate()), review.getRate());
                        assertEquals(requestDTO.getGoodSides(), review.getGoodSides());
                        assertEquals(requestDTO.getBadSides(), review.getBadSides());
                    });
        }

        @Test
        void whenWriteReviewWithInvalidHotelId_thenReturnError() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            WriteReviewRequestDTO requestDTO = WriteReviewRequestDTO.builder()
                    .hotelId(String.valueOf(-1L))
                    .content("Great hotel!")
                    .rate("8.6")
                    .goodSides("The staff was very friendly")
                    .badSides("The room was a bit small")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Hotels.WRITE_REVIEW)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetPopularHotels {
        @Test
        void whenGetPopularHotels_thenReturnHotels() {
            webTestClient.get()
                    .uri(EndPoints.Hotels.GET_POPULAR_HOTELS)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetHotelsIncludeRoomResponseDTO.class);
        }
    }
}