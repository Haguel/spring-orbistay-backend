package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.haguel.orbistay.entity.enumeration.Gender;
import dev.haguel.orbistay.entity.enumeration.Role;
import dev.haguel.orbistay.util.EndPoints;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"passport", "reviews", "recentlyViewedHotels"})
@EqualsAndHashCode(exclude = {"passport", "reviews", "recentlyViewedHotels"})
@Entity(name = "app_user")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String passwordHash;

    @Column
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String avatarUrl;

    @Column
    private LocalDate birthDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @JsonIgnore
    private Role role;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "citizenship_id")
    private Country citizenship;

    @ManyToOne
    @JoinColumn(name = "residency_id")
    private Address residency;

    @OneToOne(mappedBy = "appUser")
    @JsonIgnore
    private Passport passport;

    @OneToMany(mappedBy = "appUser")
    @JsonIgnore
    private List<Review> reviews = Collections.emptyList();

    @OneToMany(mappedBy = "appUser", orphanRemoval = true)
    @JsonIgnore
    private List<RecentlyViewedHotel> recentlyViewedHotels = Collections.emptyList();

    @OneToMany(mappedBy = "appUser")
    @JsonIgnore
    private List<Booking> bookings = Collections.emptyList();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }

        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @JsonIgnore
    public List<Hotel> getMappedRecentlyViewedHotels() {
        return recentlyViewedHotels.stream()
                .map(RecentlyViewedHotel::getHotel)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
