package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Query(value = """
        SELECT DISTINCT h.*
        FROM hotel h
        JOIN address a ON h.address_id = a.id
        JOIN country c ON a.country_id = c.id
        JOIN hotel_room hr ON hr.hotel_id = h.id
        LEFT JOIN booking b ON b.hotel_room_id = hr.id
        LEFT JOIN review r ON r.hotel_id = h.id
        WHERE (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (:country IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :country, '%')))
          AND (:peopleCount IS NULL OR hr.capacity >= CAST(:peopleCount AS INT))
          AND (:isChildrenFriendly IS NULL OR :isChildrenFriendly = hr.is_children_friendly)
          AND ((CAST(:checkIn AS DATE) IS NULL OR CAST(:checkOut AS DATE) IS NULL) 
                   OR b.id IS NULL
                   OR (CAST(:checkIn AS DATE) < b.check_in AND CAST(:checkOut AS DATE) < b.check_in)
                   OR (CAST(:checkIn AS DATE) > b.check_out AND CAST(:checkOut AS DATE) > b.check_out))
          AND (:minPrice IS NULL OR hr.cost_per_night >= :minPrice)
          AND (:maxPrice IS NULL OR hr.cost_per_night <= :maxPrice)
          AND (:minRating IS NULL OR (SELECT AVG(r.rate) FROM review r WHERE r.hotel_id = h.id) >= :minRating)
          AND (:maxRating IS NULL OR (SELECT AVG(r.rate) FROM review r WHERE r.hotel_id = h.id) <= :maxRating)
          AND (:minStars IS NULL OR h.stars >= :minStars)
          AND (:maxStars IS NULL OR h.stars <= :maxStars)
    """, nativeQuery = true)
    Optional<List<Hotel>> findHotels(@Param("name") String name,
                                     @Param("city") String city,
                                     @Param("country") String country,
                                     @Param("peopleCount") Integer peopleCount,
                                     @Param("isChildrenFriendly") Boolean isChildrenFriendly,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice,
                                     @Param("minRating") Integer minRating,
                                     @Param("maxRating") Integer maxRating,
                                     @Param("minStars") Integer minStars,
                                     @Param("maxStars") Integer maxStars);
}