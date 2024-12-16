package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.TestDataGenerator;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class HotelRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE booking, hotel_room, hotel, address, country, app_user RESTART IDENTITY CASCADE");

        Country country = new Country();
        country.setCode("US");
        country.setName("United States");
        countryRepository.save(country);

        Address address = new Address();
        address.setCity("New York");
        address.setStreet("123 Main St");
        address.setCountry(country);
        addressRepository.save(address);

        Hotel hotel = new Hotel();
        hotel.setName("Royal Respite");
        hotel.setShortDesc("A royal respite");
        hotel.setFullDesc("A royal respite for weary travelers");
        hotel.setAddress(address);
        hotelRepository.save(hotel);

        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setHotel(hotel);
        hotelRoom.setName("Royal Room");
        hotelRoom.setDescription("A royal room");
        hotelRoom.setCapacity(2);
        hotelRoom.setCostPerDay(10.0);
        hotelRoom.setIsChildrenFriendly(true);
        hotelRoomRepository.save(hotelRoom);

        AppUser appUser = TestDataGenerator.generateRandomUser();
        appUserRepository.save(appUser);

        Booking booking = new Booking();
        booking.setAppUser(appUser);
        booking.setHotelRoom(hotelRoom);
        booking.setCheckIn(LocalDate.of(2024, 12, 1));
        booking.setCheckOut(LocalDate.of(2024, 12, 10));
        bookingRepository.save(booking);
    }

    @Test
    void whenFindHotelByNameCriteria_thenReturnHotel() {
        List<Hotel> result = hotelRepository.findHotels(
                        "Royal Respite", null, null, null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Royal Respite", result.get(0).getName());
    }

    @Test
    void whenFindHotelByCountryCriteria_thenReturnHotel() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, "United States", null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Royal Respite", result.get(0).getName());
    }

    @Test
    void whenFindHotelByPeopleCountCriteria_thenReturnHotel() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, null, 2, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Royal Respite", result.get(0).getName());
    }

    @Test
    void whenFindHotelByChildrenFriendlyCriteria_thenReturnHotel() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, null, null, true, null, null)
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Royal Respite", result.get(0).getName());
    }

    @Test
    void whenFindHotelByCheckInAndCheckOutCriteria_thenReturnHotel() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, null, null, null,
                        LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 8))
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Royal Respite", result.get(0).getName());
    }

    @Test
    void whenFindHotelByNonExistentName_thenReturnEmpty() {
        List<Hotel> result = hotelRepository.findHotels(
                        "Nonexistent Hotel", null, null, null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelByNonExistentCountry_thenReturnEmpty() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, "Nonexistent Country", null, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelByInvalidPeopleCount_thenReturnEmpty() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, null, 10, null, null, null)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelByInvalidChildrenFriendly_thenReturnEmpty() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, null, null, false, null, null)
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelByTakenCheckInAndCheckOut_thenReturnEmpty() {
        List<Hotel> result = hotelRepository.findHotels(
                        null, null, null, null, null,
                        LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 10))
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        result = hotelRepository.findHotels(
                        null, null, null, null, null,
                        LocalDate.of(2024, 11, 28), LocalDate.of(2024, 12, 5))
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        result = hotelRepository.findHotels(
                        null, null, null, null, null,
                        LocalDate.of(2024, 12, 6), LocalDate.of(2024, 12, 18))
                .orElse(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenFindHotelByFullData_thenReturnHotel() {
        List<Hotel> result = hotelRepository.findHotels(
                        "Royal Respite", "New York", "United States", 2, true, LocalDate.of(2022, 12, 5), LocalDate.of(2022, 12, 8))
                .orElse(null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Royal Respite", result.get(0).getName());
    }
}