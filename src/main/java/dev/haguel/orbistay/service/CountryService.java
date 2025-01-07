package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.Country;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public Country findById(Long id)
            throws CountryNotFoundException {
        Country country = countryRepository.findById(id).orElse(null);

        if(country == null) {
            log.warn("Country couldn't be found in database by provided id: {}", id);
            throw new CountryNotFoundException("Country with provided id not found in database");
        } else {
            log.info("Country with id {} found in database", id);
        }

        return country;
    }
}
