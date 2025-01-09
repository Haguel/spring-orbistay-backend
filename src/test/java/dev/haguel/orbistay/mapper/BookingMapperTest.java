package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class BookingMapperTest {
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

            Booking booking = bookingMapper.bookHotelRoomRequestDTOToBooking(requestDTO);

            assertNotNull(booking);
            assertEquals(requestDTO.getCheckIn(), booking.getCheckIn().toString());
            assertEquals(requestDTO.getCheckOut(), booking.getCheckOut().toString());
            assertEquals(requestDTO.getFirstName(), booking.getFirstName());
            assertEquals(requestDTO.getLastName(), booking.getLastName());
            assertEquals(requestDTO.getEmail(), booking.getEmail());
            assertEquals(requestDTO.getPhoneNumber(), booking.getPhoneNumber());
        }
    }
}
