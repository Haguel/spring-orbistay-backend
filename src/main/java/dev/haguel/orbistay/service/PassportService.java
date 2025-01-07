package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.Passport;
import dev.haguel.orbistay.repository.PassportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportService {
    private final PassportRepository passportRepository;

    public Passport save(Passport passport) {
        passport = passportRepository.save(passport);

        log.info("Passport saved to database");
        return passport;
    }
}
