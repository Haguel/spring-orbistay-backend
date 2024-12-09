package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}