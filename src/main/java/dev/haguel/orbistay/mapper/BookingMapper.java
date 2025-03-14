package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.dto.response.BookingInfoResponseDTO;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.util.mapper.SharedMapperUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {
    @Autowired
    protected SharedMapperUtil sharedMapperUtil;

    @Mapping(target = "checkIn", expression = "java(sharedMapperUtil.convertStringToLocalDateTime(bookHotelRoomRequestDTO.getCheckIn(), checkInTime))")
    @Mapping(target = "checkOut", expression = "java(sharedMapperUtil.convertStringToLocalDateTime(bookHotelRoomRequestDTO.getCheckOut(), checkOutTime))")
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    @Mapping(target = "hotelRoom", ignore = true)
    @Mapping(target = "status", ignore = true)
    public abstract Booking bookHotelRoomRequestDTOToBooking(BookHotelRoomRequestDTO bookHotelRoomRequestDTO,
                                                             LocalTime checkInTime, LocalTime checkOutTime);

    @Mapping(target = "hotelRoomId", source = "hotelRoom.id")
    @Mapping(target = "hotelId", source = "hotelRoom.hotel.id")
    public abstract BookingInfoResponseDTO bookingToBookingInfoResponseDTO(Booking booking);

    @AfterMapping
    protected void validateBookingDates(@MappingTarget Booking booking) {
        if (booking.getCheckIn() == null || booking.getCheckOut() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Booking dates are invalid");
        }
    }
}
