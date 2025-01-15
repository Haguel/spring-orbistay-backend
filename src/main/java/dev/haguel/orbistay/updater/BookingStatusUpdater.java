package dev.haguel.orbistay.updater;

import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.BookingStatus;
import dev.haguel.orbistay.repository.BookingRepository;
import dev.haguel.orbistay.repository.BookingStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingStatusUpdater {

    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void updateBookingStatuses() {
        List<Booking> activeBookings = bookingRepository.findAllActiveStatus();
        LocalDate today = LocalDate.now();

        for (Booking booking : activeBookings) {
            if (today.isAfter(booking.getCheckIn()) && today.isBefore(booking.getCheckOut())) {
                updateStatus(booking, "CHECKED-IN");
            } else if (today.isAfter(booking.getCheckOut())) {
                updateStatus(booking, "CHECKED-OUT");
            }
        }
    }

    private void updateStatus(Booking booking, String status) {
        BookingStatus bookingStatus = bookingStatusRepository.findByStatus(status);
        booking.setStatus(bookingStatus);
        bookingRepository.save(booking);
        log.info("Updated booking {} status to {}", booking.getId(), status);
    }
}
