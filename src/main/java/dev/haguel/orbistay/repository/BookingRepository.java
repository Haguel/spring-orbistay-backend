package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
