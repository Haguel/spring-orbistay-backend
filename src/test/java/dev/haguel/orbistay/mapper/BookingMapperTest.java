package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.Booking;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingMapperTest extends BaseMapperTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private BookingMapper bookingMapper;

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
                    .phoneNumber("1234567890")
                    .build();

            LocalTime localTime = LocalTime.now();
            Booking booking = bookingMapper.bookHotelRoomRequestDTOToBooking(requestDTO, localTime, localTime);

            assertNotNull(booking);
            assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckIn()), localTime), booking.getCheckIn());
            assertEquals(LocalDateTime.of(LocalDate.parse(requestDTO.getCheckOut()), localTime), booking.getCheckOut());
            assertEquals(requestDTO.getFirstName(), booking.getFirstName());
            assertEquals(requestDTO.getLastName(), booking.getLastName());
            assertEquals(requestDTO.getEmail(), booking.getEmail());
            assertEquals(requestDTO.getPhoneNumber(), booking.getPhoneNumber());
        }
    }
}
