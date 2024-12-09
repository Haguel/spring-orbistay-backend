package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
}