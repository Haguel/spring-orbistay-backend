package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"images", "roomFacilities", "roomHighlights"})
@EqualsAndHashCode(exclude = {"images", "roomFacilities", "roomHighlights"})
@Entity(name = "hotel_room")
public class HotelRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double costPerDay;

    @Column(nullable = false)
    private Boolean isChildrenFriendly;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany
    private List<HotelRoomImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "hotelRoom")
    private List<HotelRoomRoomFacility> roomFacilities;

    @OneToMany(mappedBy = "hotelRoom")
    private List<HotelRoomRoomHighlight> roomHighlights;
}
