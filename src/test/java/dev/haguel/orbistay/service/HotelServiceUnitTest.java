package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.mapper.HotelMapper;
import dev.haguel.orbistay.repository.HotelRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceUnitTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelService hotelService;

    @Nested
    class GetHotelById {
        @Test
        void whenHotelExists_thenReturnHotelResponse() {
            Long id = 1L;
            Hotel hotel = new Hotel();
            GetHotelResponseDTO responseDTO = new GetHotelResponseDTO();

            when(hotelRepository.findById(id)).thenReturn(Optional.of(hotel));
            when(hotelMapper.hotelToHotelResponseDTO(hotel)).thenReturn(responseDTO);

            GetHotelResponseDTO result = hotelService.getHotelById(id);

            assertEquals(responseDTO, result);
            verify(hotelRepository).findById(id);
        }

        @Test
        void whenHotelDoesNotExist_thenThrowHotelNotFoundException() {
            Long id = 1L;
            when(hotelRepository.findById(id)).thenReturn(Optional.empty());

            HotelNotFoundException exception = assertThrows(HotelNotFoundException.class,
                    () -> hotelService.getHotelById(id));
            assertEquals("Hotel not found", exception.getMessage());
        }
    }

    @Nested
    class FindById {
        @Test
        void whenHotelExists_thenReturnHotel() {
            Long id = 1L;
            Hotel hotel = new Hotel();
            when(hotelRepository.findById(id)).thenReturn(Optional.of(hotel));

            Hotel result = hotelService.findById(id);

            assertNotNull(result);
            verify(hotelRepository).findById(id);
        }

        @Test
        void whenHotelDoesNotExist_thenThrowHotelNotFoundException() {
            Long id = 1L;
            when(hotelRepository.findById(id)).thenReturn(Optional.empty());

            HotelNotFoundException exception = assertThrows(HotelNotFoundException.class,
                    () -> hotelService.findById(id));
            assertEquals("Hotel not found", exception.getMessage());
        }
    }

    @Nested
    class GetPopularHotels {
        @Test
        void whenPopularHotelsFound_thenReturnHotelsResponse() {
            Hotel hotel = new Hotel();
            List<Hotel> hotels = List.of(hotel);
            GetHotelsResponseDTO responseDTO = new GetHotelsResponseDTO();

            when(hotelRepository.findPopularHotels(any(LocalDate.class))).thenReturn(hotels);
            when(hotelMapper.hotelToHotelsResponseDTO(hotel)).thenReturn(responseDTO);

            List<GetHotelsResponseDTO> result = hotelService.getPopularHotels();

            assertEquals(1, result.size());
            assertEquals(responseDTO, result.get(0));
            verify(hotelRepository).findPopularHotels(any(LocalDate.class));
        }
    }

    @Nested
    class GetHotelsWithAddressSimilarToText {
        @Test
        void whenHotelsFound_thenReturnHotels() {
            String text = "Test";
            Hotel hotel = new Hotel();
            List<Hotel> hotels = List.of(hotel);

            when(hotelRepository.findHotelsWithAddressSimilarToText(text)).thenReturn(hotels);

            List<Hotel> result = hotelService.getHotelsWithAddressSimilarToText(text);

            assertEquals(1, result.size());
            verify(hotelRepository).findHotelsWithAddressSimilarToText(text);
        }
    }

    @Nested
    class GetPopularHotelsRaw {
        @Test
        void whenPopularHotelsFound_thenReturnHotels() {
            Hotel hotel = new Hotel();
            List<Hotel> hotels = List.of(hotel);

            when(hotelRepository.findPopularHotels(any(LocalDate.class))).thenReturn(hotels);

            List<Hotel> result = hotelService.getPopularHotelsRaw();

            assertEquals(1, result.size());
            verify(hotelRepository).findPopularHotels(any(LocalDate.class));
        }
    }
}