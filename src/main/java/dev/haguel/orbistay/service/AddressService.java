package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.Address;
import dev.haguel.orbistay.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;

    public Address save(Address address) {
        address = addressRepository.save(address);

        log.info("Address saved to database");
        return address;
    }

    public void delete(Address address) {
        addressRepository.delete(address);
    }
}
