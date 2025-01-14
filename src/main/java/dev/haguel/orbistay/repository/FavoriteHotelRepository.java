package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteHotelRepository extends JpaRepository<Favorites, Long> {
}
