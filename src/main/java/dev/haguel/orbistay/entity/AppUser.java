package dev.haguel.orbistay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.haguel.orbistay.entity.enumeration.Gender;
import dev.haguel.orbistay.entity.enumeration.Role;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"passport"})
@EqualsAndHashCode(exclude = {"passport"})
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

    @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
