package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    @Query("SELECT f FROM favorites f WHERE f.hotel.id = :hotelId AND f.appUser.id = :appUserId")
    Optional<Favorites> findFavoritesByHotelIdAndAppUserId(Long hotelId, Long appUserId);
}
