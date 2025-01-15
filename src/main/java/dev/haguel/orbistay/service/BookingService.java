package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.Country;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.exception.BookingNotAvailableException;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.exception.HotelRoomNotFoundException;
import dev.haguel.orbistay.exception.InvalidDataException;
import dev.haguel.orbistay.mapper.BookingMapper;
import dev.haguel.orbistay.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final HotelRoomService hotelRoomService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CountryService countryService;

    private boolean isDatesValid(LocalDate checkIn, LocalDate checkOut) {
        return checkIn.isBefore(checkOut);
    }

    public Booking bookHotelRoom(AppUser appUser, BookHotelRoomRequestDTO bookHotelRoomRequestDTO)
            throws BookingNotAvailableException, HotelRoomNotFoundException, CountryNotFoundException, InvalidDataException {
        HotelRoom hotelRoom = hotelRoomService.getHotelRoomById(Long.valueOf(bookHotelRoomRequestDTO.getHotelRoomId()));
        Country country = countryService.findById(Long.valueOf(bookHotelRoomRequestDTO.getCountryId()));
        Booking booking = bookingMapper.bookHotelRoomRequestDTOToBooking(bookHotelRoomRequestDTO);

        if(!isDatesValid(booking.getCheckIn(), booking.getCheckOut())) {
            log.warn("Check-in must be before check-out");
            throw new InvalidDataException("Check-in must be before check-out");
        }

        if(bookingRepository.isBookingAvailable(hotelRoom.getId(), booking.getCheckIn(), booking.getCheckOut())) {
            log.info("Booking is available");
        } else {
            log.error("Booking is not available");
            throw new BookingNotAvailableException("Hotel room is not available for the selected dates");
        }

        booking.setAppUser(appUser);
        booking.setHotelRoom(hotelRoom);
        booking.setCountry(country);
        booking = bookingRepository.save(booking);

        log.info("Booking created and saved to db");
        return booking;
    }
}
