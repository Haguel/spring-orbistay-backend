package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.dto.response.BookingInfoResponseDTO;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.repository.BookingRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingMapperTest extends BaseMapperTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Nested
    class BookHotelRoomRequestDTOMapping{
        @Test
        public void whenBookHotelRoomRequestToBooking_thenReturnBooking() {
            BookHotelRoomRequestDTO requestDTO = BookHotelRoomRequestDTO.builder()
                    .hotelRoomId("1")
                    .countryId("1")
                    .checkIn("2023-12-01")
                    .checkOut("2023-12-10")
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.test@example.com")
                    .phone("1234567890")
                    .build();

            LocalTime localTime = LocalTime.now();
            Booking booking = bookingMapper.bookHotelRoomRequestDTOToBooking(requestDTO, localTime, localTime);

            assertNotNull(booking);
            assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckIn()), localTime), booking.getCheckIn());
            assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckOut()), localTime), booking.getCheckOut());
            assertEquals(requestDTO.getFirstName(), booking.getFirstName());
            assertEquals(requestDTO.getLastName(), booking.getLastName());
            assertEquals(requestDTO.getEmail(), booking.getEmail());
            assertEquals(requestDTO.getPhone(), booking.getPhone());
        }
    }

    @Nested
    class BookingToBookingInfoResponseDTOMapping {
        @Test
        public void whenBookingToBookingInfoResponseDTO_thenReturnBookingInfoResponseDTO() {
            Booking booking = bookingRepository.findById(1L).orElse(null);

            BookingInfoResponseDTO responseDTO = bookingMapper.bookingToBookingInfoResponseDTO(booking);

            assertNotNull(responseDTO);
            assertEquals(booking.getId(), responseDTO.getId());
            assertEquals(booking.getCheckIn(), responseDTO.getCheckIn());
            assertEquals(booking.getCheckOut(), responseDTO.getCheckOut());
            assertEquals(booking.getFirstName(), responseDTO.getFirstName());
            assertEquals(booking.getLastName(), responseDTO.getLastName());
            assertEquals(booking.getEmail(), responseDTO.getEmail());
            assertEquals(booking.getPhone(), responseDTO.getPhone());
            assertEquals(booking.getCountry().getId(), responseDTO.getCountry().getId());
            assertEquals(booking.getStatus().getId(), responseDTO.getStatus().getId());
            assertEquals(booking.getHotelRoom().getId(), responseDTO.getHotelRoomId());
            assertEquals(booking.getHotelRoom().getHotel().getId(), responseDTO.getHotelId());
            Optional.ofNullable(booking.getPayment()).ifPresent(bookingPayment ->
                    assertEquals(bookingPayment.getId(), responseDTO.getPayment().getId()));
        }
    }
}
