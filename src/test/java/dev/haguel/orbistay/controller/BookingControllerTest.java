package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.service.AppUserService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.SharedTestUtil;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class BookingControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class BookHotelRoom {
        @Test
        void whenBookHotelRoomWithValidData_thenReturnBooking() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("1")
                    .checkIn("2023-12-01")
                    .checkOut("2023-12-10")
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phoneNumber("1234567890")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Booking.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertEquals(requestDTO.getCheckIn(), response.getCheckIn().toString());
                        assertEquals(requestDTO.getCheckOut(), response.getCheckOut().toString());
                        assertEquals(requestDTO.getFirstName(), response.getFirstName());
                        assertEquals(requestDTO.getLastName(), response.getLastName());
                        assertEquals(requestDTO.getEmail(), response.getEmail());
                        assertEquals(requestDTO.getPhoneNumber(), response.getPhoneNumber());
                        assertEquals(Long.parseLong(requestDTO.getHotelRoomId()), response.getHotelRoom().getId());
                        assertEquals(Long.parseLong(requestDTO.getCountryId()), response.getCountry().getId());
                    });
        }

        @Test
        void whenBookHotelRoomWithTakenDates_thenReturnError() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("1")
                    .checkIn("2024-12-01")
                    .checkOut("2024-12-05")
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phoneNumber("1234567890")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void whenBookHotelRoomWithInvalidCountry_thenReturnError() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("400")
                    .checkIn("2024-11-10")
                    .checkOut("2024-11-11")
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phoneNumber("1234567890")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().is4xxClientError();
        }

        @Test
        void whenBookHotelRoomWithInvalidHotelRoom_thenReturnError() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("-1")
                    .countryId("1")
                    .checkIn("2024-11-10")
                    .checkOut("2024-11-11")
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phoneNumber("1234567890")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().is4xxClientError();
        }
    }

    @Nested
    class GetBookings {
        @Test
        void whenGetBookings_thenReturnBookings() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.get()
                    .uri(EndPoints.Booking.GET_BOOKINGS)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Booking.class)
                    .hasSize(4);
        }
    }
}