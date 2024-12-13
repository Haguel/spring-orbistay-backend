package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "hotel")
    private List<HotelRoom> hotelRooms;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "hotel")
    private List<HotelHotelHighlight> hotelHotelHighlights;
}
