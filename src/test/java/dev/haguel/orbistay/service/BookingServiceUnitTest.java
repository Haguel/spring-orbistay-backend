package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.*;
import dev.haguel.orbistay.exception.*;
import dev.haguel.orbistay.mapper.BookingMapper;
import dev.haguel.orbistay.repository.BookingRepository;
import dev.haguel.orbistay.repository.BookingStatusRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @Mock
    private HotelRoomService hotelRoomService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingStatusRepository bookingStatusRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private BookingService bookingService;

    private AppUser createEligibleUser() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setCitizenship(new Country());
        user.setResidency(new Address());
        Passport passport = new Passport();
        passport.setExpirationDate(LocalDate.now().plusYears(1));
        user.setPassport(passport);
        EmailVerification verification = new EmailVerification();
        verification.setVerified(true);
        user.setEmailVerification(verification);
        user.setBankCards(List.of(new BankCard()));
        return user;
    }

    private AppUser createUserWithoutCitizenship() {
        AppUser user = createEligibleUser();
        user.setCitizenship(null);
        return user;
    }

    private AppUser createUserWithoutResidency() {
        AppUser user = createEligibleUser();
        user.setResidency(null);
        return user;
    }

    private AppUser createUserWithoutPassport() {
        AppUser user = createEligibleUser();
        user.setPassport(null);
        return user;
    }

    private AppUser createUserWithUnverifiedEmail() {
        AppUser user = createEligibleUser();
        user.getEmailVerification().setVerified(false);
        return user;
    }

    private AppUser createUserWithExpiredPassport() {
        AppUser user = createEligibleUser();
        user.getPassport().setExpirationDate(LocalDate.now().minusDays(1));
        return user;
    }

    private AppUser createUserWithoutBankCards() {
        AppUser user = createEligibleUser();
        user.setBankCards(Collections.emptyList());
        return user;
    }

    @Nested
    class CheckEligibilityTests {
        @Test
        void whenAllDataPresent_thenNoException() {
            AppUser user = createEligibleUser();
            assertDoesNotThrow(() -> bookingService.checkEligibility(user));
        }

        @Test
        void whenCitizenshipMissing_thenThrowRequiredDataMissingException() {
            AppUser user = createUserWithoutCitizenship();
            RequiredDataMissingException exception = assertThrows(RequiredDataMissingException.class,
                    () -> bookingService.checkEligibility(user));
            assertTrue(exception.getMessage().contains("citizenship"));
        }

        @Test
        void whenResidencyMissing_thenThrowRequiredDataMissingException() {
            AppUser user = createUserWithoutResidency();
            RequiredDataMissingException exception = assertThrows(RequiredDataMissingException.class,
                    () -> bookingService.checkEligibility(user));
            assertTrue(exception.getMessage().contains("residency"));
        }

        @Test
        void whenPassportMissing_thenThrowRequiredDataMissingException() {
            AppUser user = createUserWithoutPassport();
            RequiredDataMissingException exception = assertThrows(RequiredDataMissingException.class,
                    () -> bookingService.checkEligibility(user));
            assertTrue(exception.getMessage().contains("passport"));
        }

        @Test
        void whenEmailNotVerified_thenThrowRequiredDataMissingException() {
            AppUser user = createUserWithUnverifiedEmail();
            RequiredDataMissingException exception = assertThrows(RequiredDataMissingException.class,
                    () -> bookingService.checkEligibility(user));
            assertEquals("Email is not verified", exception.getMessage());
        }

        @Test
        void whenPassportExpired_thenThrowPassportIsExpiredException() {
            AppUser user = createUserWithExpiredPassport();
            PassportIsExpiredException exception = assertThrows(PassportIsExpiredException.class,
                    () -> bookingService.checkEligibility(user));
            assertEquals("Passport is expired", exception.getMessage());
        }

        @Test
        void whenNoBankCards_thenThrowRequiredDataMissingException() {
            AppUser user = createUserWithoutBankCards();
            RequiredDataMissingException exception = assertThrows(RequiredDataMissingException.class,
                    () -> bookingService.checkEligibility(user));
            assertEquals("At least one bank card is required", exception.getMessage());
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void whenBookingExists_thenReturnBooking() {
            Booking booking = new Booking();
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            Booking result = bookingService.findById(1L);
            assertEquals(booking, result);
        }

        @Test
        void whenBookingDoesNotExist_thenThrowBookingNotFoundException() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
            BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                    () -> bookingService.findById(1L));
            assertEquals("Booking with not found", exception.getMessage());
        }
    }

    @Nested
    class BookHotelRoomTests {
        @Test
        void whenAllDataCorrect_thenBookSuccessfully() throws Exception {
            AppUser user = createEligibleUser();
            BookHotelRoomRequestDTO dto = new BookHotelRoomRequestDTO();
            dto.setHotelRoomId("1");
            dto.setCountryId("1");
            dto.setCheckIn("2023-10-01T14:00:00");
            dto.setCheckOut("2023-10-05T12:00:00");

            HotelRoom hotelRoom = new HotelRoom();
            hotelRoom.setId(1L);
            Hotel hotel = new Hotel();
            hotel.setCheckInTime(LocalTime.of(14, 0));
            hotel.setCheckOutTime(LocalTime.of(12, 0));
            hotelRoom.setHotel(hotel);

            Country country = new Country();
            Booking booking = new Booking();
            booking.setCheckIn(LocalDateTime.parse("2023-10-01T14:00:00"));
            booking.setCheckOut(LocalDateTime.parse("2023-10-05T12:00:00"));
            BookingStatus pendingStatus = new BookingStatus();

            when(hotelRoomService.findById(1L)).thenReturn(hotelRoom);
            when(countryService.findById(1L)).thenReturn(country);
            when(bookingMapper.bookHotelRoomRequestDTOToBooking(dto, hotel.getCheckInTime(), hotel.getCheckOutTime()))
                    .thenReturn(booking);
            when(bookingRepository.isBookingAvailable(1L, booking.getCheckIn(), booking.getCheckOut())).thenReturn(true);
            when(bookingStatusRepository.findPendingStatus()).thenReturn(pendingStatus);
            when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Booking result = bookingService.bookHotelRoom(user, dto);

            assertNotNull(result);
            assertEquals(user, result.getAppUser());
            assertEquals(hotelRoom, result.getHotelRoom());
            assertEquals(country, result.getCountry());
            assertEquals(pendingStatus, result.getStatus());
            verify(bookingRepository).save(result);
        }

        @Test
        void whenUserNotEligible_thenThrowRequiredDataMissingException() {
            AppUser user = createUserWithoutCitizenship();
            BookHotelRoomRequestDTO dto = new BookHotelRoomRequestDTO();
            RequiredDataMissingException exception = assertThrows(RequiredDataMissingException.class,
                    () -> bookingService.bookHotelRoom(user, dto));
            assertTrue(exception.getMessage().contains("citizenship"));
        }

        @Test
        void whenHotelRoomNotFound_thenThrowHotelRoomNotFoundException() throws Exception {
            AppUser user = createEligibleUser();
            BookHotelRoomRequestDTO dto = new BookHotelRoomRequestDTO();
            dto.setHotelRoomId("1");
            when(hotelRoomService.findById(1L)).thenThrow(new HotelRoomNotFoundException("Hotel room not found"));
            assertThrows(HotelRoomNotFoundException.class, () -> bookingService.bookHotelRoom(user, dto));
        }

        @Test
        void whenCountryNotFound_thenThrowCountryNotFoundException() throws Exception {
            AppUser user = createEligibleUser();
            BookHotelRoomRequestDTO dto = new BookHotelRoomRequestDTO();
            dto.setHotelRoomId("1");
            dto.setCountryId("1");
            HotelRoom hotelRoom = new HotelRoom();
            hotelRoom.setHotel(new Hotel());
            when(hotelRoomService.findById(1L)).thenReturn(hotelRoom);
            when(countryService.findById(1L)).thenThrow(new CountryNotFoundException("Country not found"));
            assertThrows(CountryNotFoundException.class, () -> bookingService.bookHotelRoom(user, dto));
        }

        @Test
        void whenCheckInAfterCheckOut_thenThrowInvalidDataException() throws Exception {
            AppUser user = createEligibleUser();
            BookHotelRoomRequestDTO dto = new BookHotelRoomRequestDTO();
            dto.setHotelRoomId("1");
            dto.setCountryId("1");
            HotelRoom hotelRoom = new HotelRoom();
            hotelRoom.setId(1L);
            Hotel hotel = new Hotel();
            hotel.setCheckInTime(LocalTime.of(14, 0));
            hotel.setCheckOutTime(LocalTime.of(12, 0));
            hotelRoom.setHotel(hotel);
            Country country = new Country();
            Booking booking = new Booking();
            booking.setCheckIn(LocalDateTime.parse("2023-10-05T14:00:00"));
            booking.setCheckOut(LocalDateTime.parse("2023-10-01T12:00:00"));

            when(hotelRoomService.findById(1L)).thenReturn(hotelRoom);
            when(countryService.findById(1L)).thenReturn(country);
            when(bookingMapper.bookHotelRoomRequestDTOToBooking(dto, hotel.getCheckInTime(), hotel.getCheckOutTime()))
                    .thenReturn(booking);

            InvalidDataException exception = assertThrows(InvalidDataException.class,
                    () -> bookingService.bookHotelRoom(user, dto));
            assertEquals("Check-in must be before check-out", exception.getMessage());
        }

        @Test
        void whenBookingNotAvailable_thenThrowBookingNotAvailableException() throws Exception {
            AppUser user = createEligibleUser();
            BookHotelRoomRequestDTO dto = new BookHotelRoomRequestDTO();
            dto.setHotelRoomId("1");
            dto.setCountryId("1");
            HotelRoom hotelRoom = new HotelRoom();
            hotelRoom.setId(1L);
            Hotel hotel = new Hotel();
            hotel.setCheckInTime(LocalTime.of(14, 0));
            hotel.setCheckOutTime(LocalTime.of(12, 0));
            hotelRoom.setHotel(hotel);
            Country country = new Country();
            Booking booking = new Booking();
            booking.setCheckIn(LocalDateTime.parse("2023-10-01T14:00:00"));
            booking.setCheckOut(LocalDateTime.parse("2023-10-05T12:00:00"));

            when(hotelRoomService.findById(1L)).thenReturn(hotelRoom);
            when(countryService.findById(1L)).thenReturn(country);
            when(bookingMapper.bookHotelRoomRequestDTOToBooking(dto, hotel.getCheckInTime(), hotel.getCheckOutTime()))
                    .thenReturn(booking);
            when(bookingRepository.isBookingAvailable(1L, booking.getCheckIn(), booking.getCheckOut())).thenReturn(false);

            BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                    () -> bookingService.bookHotelRoom(user, dto));
            assertEquals("Hotel room is not available for the selected dates", exception.getMessage());
        }
    }

    @Nested
    class SetCompletedPaymentTests {
        @Test
        void whenSetPayment_thenUpdateBooking() {
            Booking booking = new Booking();
            Payment payment = new Payment();
            BookingStatus activeStatus = new BookingStatus();

            when(bookingStatusRepository.findActiveStatus()).thenReturn(activeStatus);
            when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Booking result = bookingService.setCompletedPayment(booking, payment);

            assertEquals(payment, result.getPayment());
            assertEquals(activeStatus, result.getStatus());
            verify(bookingRepository).save(result);
        }
    }

    @Nested
    class CancelBookingTests {
        @Test
        void whenUserIsOwnerAndCheckInMoreThan24HoursAway_thenCancelSuccessfully() {
            AppUser user = new AppUser();
            user.setId(1L);
            Booking booking = new Booking();
            booking.setAppUser(user);
            booking.setCheckIn(LocalDateTime.now().plusDays(2)); // More than 24 hours away
            BookingStatus canceledStatus = new BookingStatus();

            when(bookingStatusRepository.findCanceledStatus()).thenReturn(canceledStatus);
            when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() -> bookingService.cancelBooking(user, booking));
            assertEquals(canceledStatus, booking.getStatus());
            verify(bookingRepository).save(booking);
        }

        @Test
        void whenUserIsNotOwner_thenThrowCanNotChangeOtherUserDataException() {
            AppUser user1 = new AppUser();
            user1.setId(1L);
            AppUser user2 = new AppUser();
            user2.setId(2L);
            Booking booking = new Booking();
            booking.setAppUser(user2);
            booking.setCheckIn(LocalDateTime.now().plusDays(2));

            CanNotChangeOtherUserDataException exception = assertThrows(CanNotChangeOtherUserDataException.class,
                    () -> bookingService.cancelBooking(user1, booking));
            assertEquals("User is not allowed to cancel another user's booking", exception.getMessage());
        }

        @Test
        void whenCheckInLessThan24HoursAway_thenThrowBookingCanNotBeCanceledException() {
            AppUser user = new AppUser();
            user.setId(1L);
            Booking booking = new Booking();
            booking.setAppUser(user);
            booking.setCheckIn(LocalDateTime.now().plusHours(12)); // Less than 24 hours

            BookingCanNotBeCanceledException exception = assertThrows(BookingCanNotBeCanceledException.class,
                    () -> bookingService.cancelBooking(user, booking));
            assertEquals("Booking can not be canceled already", exception.getMessage());
        }
    }
}