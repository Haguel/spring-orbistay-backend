package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}