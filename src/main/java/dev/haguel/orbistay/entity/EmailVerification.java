package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime expiresAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "app_user_id")
    @JsonIgnore
    private AppUser appUser;
}
