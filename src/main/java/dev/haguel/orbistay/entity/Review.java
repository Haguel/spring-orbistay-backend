package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.haguel.orbistay.serialization.app_user.ReviewAppUserDeserializer;
import dev.haguel.orbistay.serialization.app_user.ReviewAppUserSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.persistence.*;

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

    @Schema(implementation = ReviewAppUserSerializer.InnerSerializer.class)
    @JsonSerialize(using = ReviewAppUserSerializer.class)
    @JsonDeserialize(using = ReviewAppUserDeserializer.class)
    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;
}
