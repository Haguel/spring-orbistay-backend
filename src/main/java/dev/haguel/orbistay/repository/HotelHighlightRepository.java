package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.HotelHighlight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelHighlightRepository extends JpaRepository<HotelHighlight, Long> {
}