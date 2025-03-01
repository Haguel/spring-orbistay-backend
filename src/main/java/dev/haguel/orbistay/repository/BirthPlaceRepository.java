package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Birthplace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BirthPlaceRepository extends JpaRepository<Birthplace, Long> {
}