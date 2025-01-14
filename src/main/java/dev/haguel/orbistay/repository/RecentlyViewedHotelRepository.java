package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.RecentlyViewedHotel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RecentlyViewedHotelRepository extends JpaRepository<RecentlyViewedHotel, Long> {
}
