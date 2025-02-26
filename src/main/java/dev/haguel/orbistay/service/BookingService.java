package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.*;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.mapper.BookingMapper;
import dev.haguel.orbistay.repository.BookingRepository;
import dev.haguel.orbistay.repository.BookingStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final HotelRoomService hotelRoomService;
    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final BookingMapper bookingMapper;
    private final CountryService countryService;

    private boolean areDatesNotReversed(LocalDateTime checkIn, LocalDateTime checkOut) {
        return checkIn.isBefore(checkOut);
    }

    private Booking save(Booking booking) {
        booking = bookingRepository.save(booking);

        log.info("Booking created and saved to db");
        return booking;
    }

    private void checkAppUserForBookingAbility(AppUser appUser) {
        StringBuilder missingFields = new StringBuilder();
        if (appUser.getCitizenship() == null) {
            missingFields.append("citizenship, ");
        }
        if (appUser.getResidency() == null) {
            missingFields.append("residency, ");
        }
        if (appUser.getPassport() == null) {
            missingFields.append("passport, ");
        }
        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2);
            throw new RequiredDataMissingException("Not able to book. Required data must be filled: " + missingFields);
        }

        if(!appUser.getEmailVerification().isVerified()) {
            throw new RequiredDataMissingException("Email is not verified");
        }

        if(appUser.getPassport().getExpirationDate().isBefore(LocalDate.now())) {
            throw new PassportIsExpiredException("Passport is expired");
        }
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) throws BookingNotFoundException {
        Booking booking =  bookingRepository.findById(id).orElse(null);

        if (booking == null) {
            log.warn("Booking with id {} not found", id);
            throw new BookingNotFoundException("Booking with not found");
        } else {
            log.info("Booking with id {} found", id);
        }

        return booking;
    }

    @Transactional
    public Booking bookHotelRoom(AppUser appUser, BookHotelRoomRequestDTO bookHotelRoomRequestDTO)
            throws BookingNotAvailableException, HotelRoomNotFoundException, CountryNotFoundException, InvalidDataException {
        HotelRoom hotelRoom = hotelRoomService.findById(Long.valueOf(bookHotelRoomRequestDTO.getHotelRoomId()));
        Country country = countryService.findById(Long.valueOf(bookHotelRoomRequestDTO.getCountryId()));
        Booking booking = bookingMapper.bookHotelRoomRequestDTOToBooking(bookHotelRoomRequestDTO, hotelRoom.getCheckInTime(), hotelRoom.getCheckOutTime());

        if(!areDatesNotReversed(booking.getCheckIn(), booking.getCheckOut())) {
            log.warn("Check-in must be before check-out");
            throw new InvalidDataException("Check-in must be before check-out");
        }

        if(bookingRepository.isBookingAvailable(hotelRoom.getId(), booking.getCheckIn(), booking.getCheckOut())) {
            log.info("Booking is available");
        } else {
            log.error("Booking is not available");
            throw new BookingNotAvailableException("Hotel room is not available for the selected dates");
        }

        checkAppUserForBookingAbility(appUser);

        booking.setAppUser(appUser);
        booking.setHotelRoom(hotelRoom);
        booking.setCountry(country);
        booking.setStatus(bookingStatusRepository.findActiveStatus());
        booking = save(booking);

        log.info("Booking created and saved to db");
        return booking;
    }

    @Transactional
    public void cancelBooking(AppUser appUser, Booking booking) throws CanNotChangeOtherUserDataException, BookingCanNotBeCanceledException {
        if(booking.getAppUser().getId() != appUser.getId()) {
            log.warn("User with id {} is not allowed to cancel booking of another user", appUser.getId());
            throw new CanNotChangeOtherUserDataException("User is not allowed to cancel another user's booking");
        }

        // Booking can not be canceled if check-in is less than 24 hours from now
        if(booking.getCheckIn().minusDays(1).isBefore(LocalDateTime.now())) {
            log.warn("Booking with id {} can not be canceled already", booking.getId());
            throw new BookingCanNotBeCanceledException("Booking can not be canceled already");
        }

        BookingStatus bookingStatus = bookingStatusRepository.findCanceledStatus();
        booking.setStatus(bookingStatus);
        save(booking);
    }
}
