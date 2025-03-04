package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
    @Query(value = "SELECT * FROM payment_status WHERE status = 'COMPLETED'", nativeQuery = true)
    PaymentStatus findCompletedStatus();

    @Query(value = "SELECT * FROM payment_status WHERE status = 'ON_CHECK_IN'", nativeQuery = true)
    PaymentStatus findOnCheckInStatus();

    @Query(value = "SELECT * FROM payment_status WHERE status = 'REFUNDED'", nativeQuery = true)
    PaymentStatus findRefundedStatus();
}