package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {
    @Query("SELECT bs FROM booking_status bs WHERE bs.status = 'ACTIVE'")
    BookingStatus findActiveStatus();

    @Query("SELECT bs FROM booking_status bs WHERE bs.status = 'CANCELED'")
    BookingStatus findCanceledStatus();

    @Query("SELECT bs FROM booking_status bs WHERE bs.status = 'CHECKED-IN'")
    BookingStatus findCheckedInStatus();

    @Query("SELECT bs FROM booking_status bs WHERE bs.status = 'CHECKED-OUT'")
    BookingStatus findCheckedOutStatus();

    @Query("SELECT bs FROM booking_status bs WHERE bs.status = 'PENDING'")
    BookingStatus findPendingStatus();

    BookingStatus findByStatus(String status);
}
