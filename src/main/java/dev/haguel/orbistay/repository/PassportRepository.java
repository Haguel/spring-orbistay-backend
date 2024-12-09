package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportRepository extends JpaRepository<Passport, Long> {
}