package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "country_of_issuance_id", nullable = false)
    private Country countryOfIssuance;

    @Column(nullable = false)
    private Date dateOfIssue;

    @Column(nullable = false)
    private Date expirationDate;

    @Column(nullable = false)
    private String holderFullName;

    @Column(nullable = false)
    private Date dateOfBirth;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String gender;

    @ManyToOne
    @JoinColumn(name = "place_of_birth_id")
    private PlaceOfBirth placeOfBirth;

    @OneToOne(mappedBy = "passport")
    private AppUser appUser;
}
