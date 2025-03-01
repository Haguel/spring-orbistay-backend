package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}