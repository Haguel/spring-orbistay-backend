package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ToString(exclude = {"images", "hotelRoomRoomFacilities", "hotelRoomRoomHighlights", "hotelRoomRoomBeds"})
@EqualsAndHashCode(exclude = {"images", "hotelRoomRoomFacilities", "hotelRoomRoomHighlights", "hotelRoomRoomBeds"})
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
    private Double costPerNight;

    @Column(nullable = false)
    private Double area;

    @Column(nullable = false)
    private Boolean child_friendly;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;

    @OneToMany(mappedBy = "hotelRoom")
    @JsonIgnore
    private List<HotelRoomImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "hotelRoom")
    private List<RoomFacilityLink> roomFacilityLinks;

    @OneToMany(mappedBy = "hotelRoom")
    private List<RoomHighlightLink> roomHighlightLinks;

    @OneToMany(mappedBy = "hotelRoom", fetch = FetchType.EAGER)
    private List<RoomBedLink> roomBedLinks;

    @JsonProperty("roomFacilityLinks")
    public List<Facility> getRoomFacilities() {
        return roomFacilityLinks.stream()
                .map(RoomFacilityLink::getFacility)
                .collect(Collectors.toList());
    }

    @JsonProperty("roomHighlightLinks")
    public List<Highlight> getRoomHighlights() {
        return roomHighlightLinks.stream()
                .map(RoomHighlightLink::getHighlight)
                .collect(Collectors.toList());
    }

    @JsonProperty("roomBedLinks")
    public List<BedType> getRoomBeds() {
        return roomBedLinks.stream()
                .map(RoomBedLink::getBedType)
                .collect(Collectors.toList());
    }

    @JsonProperty("images")
    public List<String> getImagesUrls() {
        return images.stream()
                .map(HotelRoomImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
