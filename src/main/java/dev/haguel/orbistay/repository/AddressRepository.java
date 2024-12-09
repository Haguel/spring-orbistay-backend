package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
