package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.haguel.orbistay.serialization.app_user.ReviewAppUserDeserializer;
import dev.haguel.orbistay.serialization.app_user.ReviewAppUserSerializer;
import dev.haguel.orbistay.serialization.hotel.ReviewHotelDeserializer;
import dev.haguel.orbistay.serialization.hotel.ReviewHotelSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rate;

    @Column
    private String content;

    @Column
    private String goodSides; // what was good about the hotel

    @Column
    private String badSides; // what was bad about the hotel

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(implementation = ReviewAppUserSerializer.InnerSerializer.class)
    @JsonSerialize(using = ReviewAppUserSerializer.class)
    @JsonDeserialize(using = ReviewAppUserDeserializer.class)
    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @Schema(implementation = ReviewHotelSerializer.InnerSerializer.class)
    @JsonSerialize(using = ReviewHotelSerializer.class)
    @JsonDeserialize(using = ReviewHotelDeserializer.class)
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
