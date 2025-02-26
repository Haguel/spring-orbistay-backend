package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.haguel.orbistay.entity.enumeration.Gender;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "passport")
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String passportNumber;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "country_of_issuance_id", nullable = false)
    private Country countryOfIssuance;

    @OneToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonBackReference
    private AppUser appUser;
}
