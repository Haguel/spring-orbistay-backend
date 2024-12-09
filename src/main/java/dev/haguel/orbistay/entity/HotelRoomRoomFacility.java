package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hotel_room_room_facility")
public class HotelRoomRoomFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_room_id", nullable = false)
    private HotelRoom hotelRoom;

    @ManyToOne
    @JoinColumn(name = "room_facility_id", nullable = false)
    private RoomFacility roomFacility;
}
