package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
