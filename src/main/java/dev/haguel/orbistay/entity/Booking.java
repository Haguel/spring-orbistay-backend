package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.haguel.orbistay.serialization.hotel_room.BookingHotelRoomDeserializer;
import dev.haguel.orbistay.serialization.hotel_room.BookingHotelRoomSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonIgnore
    private AppUser appUser;

    @ManyToOne
    @Schema(implementation = BookingHotelRoomSerializer.InnerSerializer.class)
    @JsonSerialize(using = BookingHotelRoomSerializer.class)
    @JsonDeserialize(using = BookingHotelRoomDeserializer.class)
    @JoinColumn(name = "hotel_room_id", nullable = false)
    private HotelRoom hotelRoom;
}
