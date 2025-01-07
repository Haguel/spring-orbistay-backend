package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    @Query(value = """
        SELECT DISTINCT hr.*
        FROM hotel_room hr
        LEFT JOIN booking b ON b.hotel_room_id = hr.id
        WHERE (hr.hotel_id = :hotelId)
          AND (:peopleCount IS NULL OR hr.capacity >= CAST(:peopleCount AS INT))
          AND (:isChildrenFriendly IS NULL OR :isChildrenFriendly = hr.is_children_friendly)
          AND ((CAST(:checkIn AS DATE) IS NULL OR CAST(:checkOut AS DATE) IS NULL)
                   OR b.id IS NULL
                   OR (CAST(:checkIn AS DATE) < b.check_in AND CAST(:checkOut AS DATE) < b.check_in)
                   OR (CAST(:checkIn AS DATE) > b.check_out AND CAST(:checkOut AS DATE) > b.check_out))
          AND (:minPrice IS NULL OR hr.cost_per_night >= :minPrice)
          AND (:maxPrice IS NULL OR hr.cost_per_night <= :maxPrice)
    """, nativeQuery = true)
    Optional<List<HotelRoom>> findHotelRooms(@Param("hotelId") Long hotelId,
                                     @Param("peopleCount") Integer peopleCount,
                                     @Param("isChildrenFriendly") Boolean isChildrenFriendly,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice);

    @Query(value = """
        SELECT DISTINCT hr.*
        FROM hotel_room hr
        LEFT JOIN booking b ON b.hotel_room_id = hr.id
        WHERE (hr.hotel_id = :hotelId)
          AND (:peopleCount IS NULL OR hr.capacity >= CAST(:peopleCount AS INT))
          AND (:isChildrenFriendly IS NULL OR :isChildrenFriendly = hr.is_children_friendly)
          AND ((CAST(:checkIn AS DATE) IS NULL OR CAST(:checkOut AS DATE) IS NULL)
                   OR b.id IS NULL
                   OR (CAST(:checkIn AS DATE) < b.check_in AND CAST(:checkOut AS DATE) < b.check_in)
                   OR (CAST(:checkIn AS DATE) > b.check_out AND CAST(:checkOut AS DATE) > b.check_out))
          AND (:minPrice IS NULL OR hr.cost_per_night >= :minPrice)
          AND (:maxPrice IS NULL OR hr.cost_per_night <= :maxPrice)
        LIMIT 1
    """, nativeQuery = true)
    Optional<HotelRoom> findHotelRoom(@Param("hotelId") Long hotelId,
                                             @Param("peopleCount") Integer peopleCount,
                                             @Param("isChildrenFriendly") Boolean isChildrenFriendly,
                                             @Param("checkIn") LocalDate checkIn,
                                             @Param("checkOut") LocalDate checkOut,
                                             @Param("minPrice") Double minPrice,
                                             @Param("maxPrice") Double maxPrice);
}