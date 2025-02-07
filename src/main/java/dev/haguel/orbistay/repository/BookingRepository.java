package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN false ELSE true END " +
            "FROM booking b " +
            "WHERE b.hotel_room_id = :hotelRoomId " +
            "AND (:checkIn BETWEEN b.check_in AND b.check_out " +
            "OR :checkOut BETWEEN b.check_in AND b.check_out " +
            "OR b.check_in BETWEEN :checkIn AND :checkOut)",
            nativeQuery = true)
    boolean isBookingAvailable(@Param("hotelRoomId") Long hotelRoomId,
                               @Param("checkIn") LocalDateTime checkIn,
                               @Param("checkOut") LocalDateTime checkOut);

    @Query("SELECT b FROM booking b WHERE b.status.status = 'ACTIVE'")
    List<Booking> findAllActiveStatus();
}
