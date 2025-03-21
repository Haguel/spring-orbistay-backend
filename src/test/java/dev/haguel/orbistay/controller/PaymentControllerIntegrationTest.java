package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.dto.request.BookingPaymentRequestDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.repository.RoomRepository;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;

import java.time.LocalDate;

public class PaymentControllerIntegrationTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private RoomRepository roomRepository;

    @Nested
    class PayBooking {
        @Test
        void whenPayingBookingWithCash_return200() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            HotelRoom room = roomRepository.findRoomWithCashPaymentOption();

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(room.getId().toString())
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phone("1234567890")
                    .build();

            Booking booking = webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Booking.class)
                    .returnResult()
                    .getResponseBody();

            BookingPaymentRequestDTO bookingPaymentRequestDTO = new BookingPaymentRequestDTO();
            bookingPaymentRequestDTO.setAmount(room.getCostPerNight() * 5);
            bookingPaymentRequestDTO.setCurrency("USD");
            bookingPaymentRequestDTO.setPaymentMethod("CASH");
            bookingPaymentRequestDTO.setBookingId(booking.getId());

            webTestClient.post()
                    .uri(EndPoints.Payment.PAY_BOOKING)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(bookingPaymentRequestDTO)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenPayingBookingWithCard_return200() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            HotelRoom room = roomRepository.findById(1L).orElse(null);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(room.getId().toString())
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phone("1234567890")
                    .build();

            Booking booking = webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Booking.class)
                    .returnResult()
                    .getResponseBody();

            BookingPaymentRequestDTO bookingPaymentRequestDTO = new BookingPaymentRequestDTO();
            bookingPaymentRequestDTO.setAmount(room.getCostPerNight() * 5);
            bookingPaymentRequestDTO.setCurrency("USD");
            bookingPaymentRequestDTO.setPaymentMethod("CARD");
            bookingPaymentRequestDTO.setBookingId(booking.getId());

            webTestClient.post()
                    .uri(EndPoints.Payment.PAY_BOOKING)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(bookingPaymentRequestDTO)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenPayingBookingWithCashToCardOnlyHotel_return400() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            HotelRoom room = roomRepository.findRoomWithOnlyCardPaymentOption();

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(room.getId().toString())
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phone("1234567890")
                    .build();

            Booking booking = webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Booking.class)
                    .returnResult()
                    .getResponseBody();

            BookingPaymentRequestDTO bookingPaymentRequestDTO = new BookingPaymentRequestDTO();
            bookingPaymentRequestDTO.setAmount(room.getCostPerNight() * 5);
            bookingPaymentRequestDTO.setCurrency("USD");
            bookingPaymentRequestDTO.setPaymentMethod("CASH");
            bookingPaymentRequestDTO.setBookingId(booking.getId());

            webTestClient.post()
                    .uri(EndPoints.Payment.PAY_BOOKING)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(bookingPaymentRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void whenPayingPaid_return400() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            HotelRoom room = roomRepository.findById(1L).orElse(null);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(room.getId().toString())
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phone("1234567890")
                    .build();

            Booking booking = webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Booking.class)
                    .returnResult()
                    .getResponseBody();

            BookingPaymentRequestDTO bookingPaymentRequestDTO = new BookingPaymentRequestDTO();
            bookingPaymentRequestDTO.setAmount(room.getCostPerNight() * 5);
            bookingPaymentRequestDTO.setCurrency("USD");
            bookingPaymentRequestDTO.setPaymentMethod("CARD");
            bookingPaymentRequestDTO.setBookingId(booking.getId());

            webTestClient.post()
                    .uri(EndPoints.Payment.PAY_BOOKING)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(bookingPaymentRequestDTO)
                    .exchange()
                    .expectStatus().isOk();

            webTestClient.post()
                    .uri(EndPoints.Payment.PAY_BOOKING)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(bookingPaymentRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        void whenPayingInvalidAmount_return400() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            HotelRoom room = roomRepository.findById(1L).orElse(null);

            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId(room.getId().toString())
                    .countryId("1")
                    .checkIn(LocalDate.now().plusDays(5).toString())
                    .checkOut(LocalDate.now().plusDays(10).toString())
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phone("1234567890")
                    .build();

            Booking booking = webTestClient.post()
                    .uri(EndPoints.Booking.BOOK_HOTEL_ROOM)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Booking.class)
                    .returnResult()
                    .getResponseBody();

            BookingPaymentRequestDTO bookingPaymentRequestDTO = new BookingPaymentRequestDTO();
            bookingPaymentRequestDTO.setAmount(1D);
            bookingPaymentRequestDTO.setCurrency("USD");
            bookingPaymentRequestDTO.setPaymentMethod("CARD");
            bookingPaymentRequestDTO.setBookingId(booking.getId());

            webTestClient.post()
                    .uri(EndPoints.Payment.PAY_BOOKING)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(bookingPaymentRequestDTO)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}
