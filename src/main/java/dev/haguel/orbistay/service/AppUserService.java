package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUser save(AppUser appUser) throws DataIntegrityViolationException {
        appUser = appUserRepository.save(appUser);

        log.info("User saved to database");
        return appUser;
    }

    public AppUser findByEmail(String email) {
        AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);

        if(appUser == null) {
            log.warn("User couldn't be found in database by provided email");
        } else {
            log.info("User found in database by provided email");
        }

        return appUser;
    }

    public AppUser findById(Long id) throws AppUserNotFoundException {
        AppUser appUser = appUserRepository.findById(id).orElse(null);

        if(appUser == null) {
            log.warn("User couldn't be found in database by provided id: {}", id);
            throw new AppUserNotFoundException("User with provided id not found in database");
        } else {
            log.info("User with id {} found in database", id);
        }

        return appUser;
    }
}
