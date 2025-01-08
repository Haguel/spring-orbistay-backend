package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.BookHotelRoomRequestDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    public void bookHotelRoom(AppUser appUser, BookHotelRoomRequestDTO bookHotelRoomRequestDTO) {

    }
}
