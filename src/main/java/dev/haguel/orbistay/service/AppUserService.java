package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
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

        log.info("User saved to databased");
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
}
