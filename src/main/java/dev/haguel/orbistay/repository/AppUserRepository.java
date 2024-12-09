package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
