package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighlightRepository extends JpaRepository<Highlight, Long> {
}