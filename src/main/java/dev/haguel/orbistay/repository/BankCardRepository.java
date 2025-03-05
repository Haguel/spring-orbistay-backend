package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {
}
