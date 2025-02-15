package dev.haguel.orbistay.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"appUser"})
@ToString(exclude = {"appUser"})
@Entity(name = "email_verification")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column(nullable = false)
    private boolean isVerified;

    @Column(nullable = false)
    private boolean isExpired;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
}
