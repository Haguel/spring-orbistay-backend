package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonBackReference
    private Hotel hotel;

    @OneToMany(mappedBy = "hotelRoom")
    @JsonManagedReference
    private List<HotelRoomImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "hotelRoom")
    private List<HotelRoomRoomFacility> hotelRoomRoomFacilities;

    @OneToMany(mappedBy = "hotelRoom")
    private List<HotelRoomRoomHighlight> hotelRoomRoomHighlights;

    @JsonProperty("hotelRoomRoomFacilities")
    public List<RoomFacility> getRoomFacilities() {
        return hotelRoomRoomFacilities.stream()
                .map(HotelRoomRoomFacility::getRoomFacility)
                .collect(Collectors.toList());
    }

    @JsonProperty("hotelRoomRoomHighlights")
    public List<RoomHighlight> getRoomHighlights() {
        return hotelRoomRoomHighlights.stream()
                .map(HotelRoomRoomHighlight::getRoomHighlight)
                .collect(Collectors.toList());
    }

    public List<String> getImagesUrls() {
        return images.stream()
                .map(HotelRoomImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
