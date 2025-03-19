package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.exception.HotelRoomNotFoundException;
import dev.haguel.orbistay.exception.HotelRoomsNotFoundException;
import dev.haguel.orbistay.repository.RoomRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelRoomServiceUnitTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private HotelRoomService hotelRoomService;

    @Nested
    class GetFilteredHotelRooms {
        @Test
        void whenRoomsFound_thenReturnListOfRooms() {
            GetFileredHotelRoomsRequestDTO dto = new GetFileredHotelRoomsRequestDTO();
            dto.setHotelId("1");
            dto.setPeopleCount("2");
            dto.setIsChildrenFriendly("true");
            dto.setCheckIn("2023-10-01");
            dto.setCheckOut("2023-10-05");
            dto.setMinPrice("100.0");
            dto.setMaxPrice("200.0");

            List<HotelRoom> rooms = List.of(new HotelRoom(), new HotelRoom());
            when(roomRepository.findHotelRooms(
                    eq(1L),
                    eq(2),
                    eq(true),
                    eq(LocalDate.parse("2023-10-01").atStartOfDay()),
                    eq(LocalDate.parse("2023-10-05").atStartOfDay()),
                    eq(100.0),
                    eq(200.0)
            )).thenReturn(Optional.of(rooms));

            List<HotelRoom> result = hotelRoomService.getFilteredHotelRooms(dto);

            assertEquals(2, result.size());
            verify(roomRepository).findHotelRooms(
                    eq(1L),
                    eq(2),
                    eq(true),
                    eq(LocalDate.parse("2023-10-01").atStartOfDay()),
                    eq(LocalDate.parse("2023-10-05").atStartOfDay()),
                    eq(100.0),
                    eq(200.0)
            );
        }

        @Test
        void whenNoRoomsFound_thenThrowHotelRoomsNotFoundException() {
            GetFileredHotelRoomsRequestDTO dto = new GetFileredHotelRoomsRequestDTO();
            dto.setCheckIn("2025-10-20");
            dto.setCheckOut("2025-10-25");
            dto.setHotelId("1");

            when(roomRepository.findHotelRooms(anyLong(), isNull(), isNull(), any(LocalDateTime.class), any(LocalDateTime.class), isNull(), isNull()))
                    .thenReturn(Optional.empty());

            HotelRoomsNotFoundException exception = assertThrows(HotelRoomsNotFoundException.class,
                    () -> hotelRoomService.getFilteredHotelRooms(dto));
            assertEquals("No hotel rooms found for given criteria", exception.getMessage());
        }
    }

    @Nested
    class GetHotelRoom {
        @Test
        void whenRoomFound_thenReturnRoom() {
            GetFileredHotelRoomsRequestDTO dto = new GetFileredHotelRoomsRequestDTO();
            dto.setHotelId("1");
            dto.setPeopleCount("2");
            dto.setIsChildrenFriendly("true");
            dto.setCheckIn("2023-10-01");
            dto.setCheckOut("2023-10-05");
            dto.setMinPrice("100.0");
            dto.setMaxPrice("200.0");

            HotelRoom room = new HotelRoom();
            when(roomRepository.findHotelRoom(
                    eq(1L),
                    eq(2),
                    eq(true),
                    eq(LocalDate.parse("2023-10-01")),
                    eq(LocalDate.parse("2023-10-05")),
                    eq(100.0),
                    eq(200.0)
            )).thenReturn(Optional.of(room));

            HotelRoom result = hotelRoomService.getHotelRoom(dto);

            assertNotNull(result);
            verify(roomRepository).findHotelRoom(
                    eq(1L),
                    eq(2),
                    eq(true),
                    eq(LocalDate.parse("2023-10-01")),
                    eq(LocalDate.parse("2023-10-05")),
                    eq(100.0),
                    eq(200.0)
            );
        }

        @Test
        void whenNoRoomFound_thenThrowHotelRoomNotFoundException() {
            GetFileredHotelRoomsRequestDTO dto = new GetFileredHotelRoomsRequestDTO();
            dto.setHotelId("1");

            when(roomRepository.findHotelRoom(anyLong(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(Optional.empty());

            HotelRoomNotFoundException exception = assertThrows(HotelRoomNotFoundException.class,
                    () -> hotelRoomService.getHotelRoom(dto));
            assertEquals("No hotel rooms found for given criteria", exception.getMessage());
        }
    }

    @Nested
    class FindById {
        @Test
        void whenRoomExists_thenReturnRoom() {
            HotelRoom room = new HotelRoom();
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            HotelRoom result = hotelRoomService.findById(1L);

            assertNotNull(result);
            verify(roomRepository).findById(1L);
        }

        @Test
        void whenRoomDoesNotExist_thenThrowHotelRoomNotFoundException() {
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            HotelRoomNotFoundException exception = assertThrows(HotelRoomNotFoundException.class,
                    () -> hotelRoomService.findById(1L));
            assertEquals("Hotel room with provided id not found in database", exception.getMessage());
        }
    }
}