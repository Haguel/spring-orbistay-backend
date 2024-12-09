package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "room_facility")
public class RoomFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "roomFacility")
    private List<HotelRoomRoomFacility> hotelRoomRoomFacilities;
}
