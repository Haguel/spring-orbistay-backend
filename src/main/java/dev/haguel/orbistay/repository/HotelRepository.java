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
      AND (:countryId IS NULL OR c.id = :countryId)
      AND (:peopleCount IS NULL OR hr.capacity >= CAST(:peopleCount AS INT))
      AND (:isChildrenFriendly IS NULL OR :isChildrenFriendly = hr.is_children_friendly)
      AND (:minPrice IS NULL OR hr.cost_per_night >= :minPrice)
      AND (:maxPrice IS NULL OR hr.cost_per_night <= :maxPrice)
      AND NOT EXISTS (
          SELECT 1
          FROM booking b
          WHERE b.hotel_room_id = hr.id
            AND :checkOut > b.check_in
            AND :checkIn < b.check_out
      )
    """, nativeQuery = true)
    Optional<List<Hotel>> findFilteredHotels(@Param("name") String name,
                                             @Param("city") String city,
                                             @Param("countryId") Long countryId,
                                             @Param("peopleCount") Integer peopleCount,
                                             @Param("isChildrenFriendly") Boolean isChildrenFriendly,
                                             @Param("checkIn") LocalDate checkIn,
                                             @Param("checkOut") LocalDate checkOut,
                                             @Param("minPrice") Double minPrice,
                                             @Param("maxPrice") Double maxPrice);

    @Query(value = """
        SELECT h.*
        FROM hotel h
        JOIN hotel_room hr ON hr.hotel_id = h.id
        JOIN booking b ON b.hotel_room_id = hr.id
        WHERE b.check_in >= :recentDate
        GROUP BY h.id
        ORDER BY COUNT(b.id) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Hotel> findPopularHotels(@Param("recentDate") LocalDate recentDate);
}