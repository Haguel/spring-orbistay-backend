package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.exception.HotelRoomNotFoundException;
import dev.haguel.orbistay.service.HotelRoomService;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private HotelRoomService hotelRoomService;

    @Nested
    class BookHotelRoom {
        @Test
        void whenBookHotelRoomWithValidData_thenReturnBooking() throws HotelRoomNotFoundException {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            Long hotelRoomId = 1L;
            HotelRoom hotelRoom = hotelRoomService.findById(hotelRoomId);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(hotelRoomId.toString())
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
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
                        assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckIn()), hotelRoom.getCheckInTime()),
                                response.getCheckIn());
                        assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckOut()), hotelRoom.getCheckOutTime()),
                                response.getCheckOut());
                        assertEquals(requestDTO.getFirstName(), response.getFirstName());
                        assertEquals(requestDTO.getLastName(), response.getLastName());
                        assertEquals(requestDTO.getEmail(), response.getEmail());
                        assertEquals(requestDTO.getPhoneNumber(), response.getPhoneNumber());
                        assertEquals(Long.parseLong(requestDTO.getHotelRoomId()), response.getHotelRoom().getId());
                        assertEquals(Long.parseLong(requestDTO.getCountryId()), response.getCountry().getId());
                        assertEquals("ACTIVE", response.getStatus().getStatus());
                    });
        }

        @Test
        void whenBookHotelRoomWithSameDayAsOtherCheckOut_thenReturnBooking() throws HotelRoomNotFoundException {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            Long hotelRoomId = 1L;
            HotelRoom hotelRoom = hotelRoomService.findById(hotelRoomId);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(hotelRoomId.toString())
                    .countryId("1")
                    .checkIn("2025-12-10")
                    .checkOut("2025-12-15")
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
                        assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckIn()), hotelRoom.getCheckInTime()),
                                response.getCheckIn());
                        assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckOut()), hotelRoom.getCheckOutTime()),
                                response.getCheckOut());
                        assertEquals(requestDTO.getFirstName(), response.getFirstName());
                        assertEquals(requestDTO.getLastName(), response.getLastName());
                        assertEquals(requestDTO.getEmail(), response.getEmail());
                        assertEquals(requestDTO.getPhoneNumber(), response.getPhoneNumber());
                        assertEquals(Long.parseLong(requestDTO.getHotelRoomId()), response.getHotelRoom().getId());
                        assertEquals(Long.parseLong(requestDTO.getCountryId()), response.getCountry().getId());
                        assertEquals("ACTIVE", response.getStatus().getStatus());
                    });
        }

        @Test
        void whenBookHotelRoomWithTakenDates_thenReturn409() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("1")
                    .checkIn("2025-12-01")
                    .checkOut("2025-12-05")
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
        void whenBookHotelRoomWithInvalidCountry_thenReturn404() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("-1")
                    .checkIn(LocalDate.now().plusDays(6).toString())
                    .checkOut(LocalDate.now().plusDays(20).toString())
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
                    .expectStatus().isNotFound();
        }

        @Test
        void whenBookHotelRoomWithInvalidHotelRoom_thenReturn404() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("-1")
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
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
                    .expectStatus().isNotFound();
        }

        @Test
        void whenBookHotelRoomWithReversedDates_thenReturn400() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(10).toString())
                    .checkOut(LocalDate.now().plusDays(5).toString())
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
                    .expectStatus().isBadRequest();
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
                    .hasSize(7);
        }
    }

    @Nested
    class CancelBooking {
        @Test
        void whenCancelBooking_thenReturnBooking() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Booking.CANCEL_BOOKING + "/1")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenCancelBookingWithInvalidId_thenReturn404() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Booking.CANCEL_BOOKING + "/-1")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void whenCancelBookingOfOtherUser_thenReturn403() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Booking.CANCEL_BOOKING + "/5")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        void whenCancelBookingWithCheckInLessThan24Hours_thenReturn400() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Booking.CANCEL_BOOKING + "/5")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }
}