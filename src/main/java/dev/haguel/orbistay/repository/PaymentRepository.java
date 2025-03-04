package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
