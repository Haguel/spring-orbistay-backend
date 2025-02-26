package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"hotelRooms", "hotelHotelHighlights"})
@EqualsAndHashCode(exclude = {"hotelRooms", "hotelHotelHighlights"})
@Entity(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String shortDesc;

    @Column(nullable = false)
    private String fullDesc;

    @Column
    private Integer stars;

    @Column
    private String mainImageUrl;

    @Column(nullable = false)
    private LocalTime checkInTime;

    @Column(nullable = false)
    private LocalTime checkOutTime;

    @OneToMany(mappedBy = "hotel")
    @JsonIgnore
    private List<HotelImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    @JsonManagedReference
    private List<HotelRoom> hotelRooms;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "hotel")
    private List<HotelHotelHighlight> hotelHotelHighlights;

    @OneToMany(mappedBy = "hotel")
    private List<Review> reviews;

    @JsonProperty("hotelHotelHighlights")
    public List<HotelHighlight> getHotelHighlights() {
        return hotelHotelHighlights.stream()
                .map(HotelHotelHighlight::getHotelHighlight)
                .collect(Collectors.toList());
    }

    public double getAvgRate() {
        return reviews.stream()
                .mapToDouble(Review::getRate)
                .average()
                .orElse(0);
    }

    @JsonProperty("images")
    public List<String> getImagesUrls() {
        return images.stream()
                .map(HotelImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
