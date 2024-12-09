package dev.haguel.orbistay.entity;

import lombok.*;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"passport"})
@EqualsAndHashCode(exclude = {"passport"})
@Entity(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "citizenship_id")
    private Country citizenship;

    @ManyToOne
    @JoinColumn(name = "residency_id")
    private Address residency;

    @OneToOne
    @JoinColumn(name = "passport_id")
    private Passport passport;
}
