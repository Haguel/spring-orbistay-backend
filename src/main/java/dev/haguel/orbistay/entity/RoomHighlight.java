package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "room_highlight")
public class RoomHighlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "roomHighlight")
    private List<HotelRoomRoomHighlight> hotelRoomRoomHighlights;
}
