package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<HotelRoom, Long> {
    @Query(value = """
    SELECT DISTINCT hr.*
    FROM hotel_room hr
    WHERE (hr.hotel_id = :hotelId)
      AND (:peopleCount IS NULL OR hr.capacity >= CAST(:peopleCount AS INT))
      AND (:isChildrenFriendly IS NULL OR :isChildrenFriendly = hr.child_friendly)
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
    Optional<List<HotelRoom>> findHotelRooms(@Param("hotelId") Long hotelId,
                                     @Param("peopleCount") Integer peopleCount,
                                     @Param("isChildrenFriendly") Boolean isChildrenFriendly,
                                     @Param("checkIn") LocalDateTime checkIn,
                                     @Param("checkOut") LocalDateTime checkOut,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice);

    @Query(value = """
    SELECT DISTINCT hr.*
    FROM hotel_room hr
    WHERE (hr.hotel_id = :hotelId)
      AND (:peopleCount IS NULL OR hr.capacity >= CAST(:peopleCount AS INT))
      AND (:isChildrenFriendly IS NULL OR :isChildrenFriendly = hr.child_friendly)
      AND (:minPrice IS NULL OR hr.cost_per_night >= :minPrice)
      AND (:maxPrice IS NULL OR hr.cost_per_night <= :maxPrice)
      AND NOT EXISTS (
          SELECT 1
          FROM booking b
          WHERE b.hotel_room_id = hr.id
            AND :checkOut > b.check_in
            AND :checkIn < b.check_out
      )
    LIMIT 1
    """, nativeQuery = true)
    Optional<HotelRoom> findHotelRoom(@Param("hotelId") Long hotelId,
                                      @Param("peopleCount") Integer peopleCount,
                                      @Param("isChildrenFriendly") Boolean isChildrenFriendly,
                                      @Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut,
                                      @Param("minPrice") Double minPrice,
                                      @Param("maxPrice") Double maxPrice);

    @Query(value = """
        SELECT hr.*
        FROM hotel_room hr
        JOIN hotel_booking_payment_option_link hbp ON hr.hotel_id = hbp.hotel_id
        JOIN booking_payment_option bpo ON hbp.booking_payment_option_id = bpo.id
        WHERE bpo.option = 'CASH'
        LIMIT 1;
    """ , nativeQuery = true)
    HotelRoom findRoomWithCashPaymentOption();

    @Query(value = """
        SELECT hr.*
        FROM hotel_room hr
        JOIN hotel_booking_payment_option_link hbp ON hr.hotel_id = hbp.hotel_id
        JOIN booking_payment_option bpo ON hbp.booking_payment_option_id = bpo.id
        WHERE bpo.option = 'CARD'
          AND NOT EXISTS (
              SELECT 1
              FROM hotel_booking_payment_option_link hbp2
              JOIN booking_payment_option bpo2 ON hbp2.booking_payment_option_id = bpo2.id
              WHERE hbp2.hotel_id = hr.hotel_id
                AND bpo2.option != 'CARD'
          )
        LIMIT 1;
    """ , nativeQuery = true)
    HotelRoom findRoomWithOnlyCardPaymentOption();
}