package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.entity.Address;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Country;
import dev.haguel.orbistay.entity.Passport;
import dev.haguel.orbistay.entity.enumeration.Gender;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.mapper.AddressMapper;
import dev.haguel.orbistay.mapper.PassportMapper;
import dev.haguel.orbistay.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final CountryService countryService;
    private final AddressService addressService;
    private final PassportService passportService;
    private final AddressMapper addressMapper;
    private final PassportMapper passportMapper;

    public AppUser save(AppUser appUser)
            throws DataIntegrityViolationException {
        appUser = appUserRepository.save(appUser);

        log.info("User saved to database");
        return appUser;
    }

    @Transactional(readOnly = true)
    public AppUser findByEmail(String email) {
        AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);

        if(appUser == null) {
            log.warn("User couldn't be found in database by provided email");
        } else {
            log.info("User found in database by provided email");
        }

        return appUser;
    }

    @Transactional(readOnly = true)
    public AppUser findById(Long id)
            throws AppUserNotFoundException {
        AppUser appUser = appUserRepository.findById(id).orElse(null);

        if(appUser == null) {
            log.warn("User couldn't be found in database by provided id: {}", id);
            throw new AppUserNotFoundException("User with provided id not found in database");
        } else {
            log.info("User with id {} found in database", id);
        }

        return appUser;
    }

    @Transactional
    public AppUser editAppUserData(AppUser appUser, EditAppUserDataRequestDTO data)
            throws CountryNotFoundException {
        if(appUser == null) {
            log.error("Provided null user");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Provided null user");
        }

        LocalDate birthDate = Optional.ofNullable(data.getBirthDate())
                .map(LocalDate::parse).orElse(null);
        Gender gender = Optional.ofNullable(data.getGender())
                .map(Gender::valueOf).orElse(null);
        Address address = Optional.ofNullable(data.getAddress())
                .map(addressMapper::addressDataRequestDTOToAddress).orElse(null);
        Passport passport = Optional.ofNullable(data.getPassport())
                .map(passportMapper::passportDataRequestDTOToPassport)
                .map((Passport pass) -> {
                    pass.setAppUser(appUser);
                    return pass;
                }).orElse(null);
        Country country = data.getCitizenshipCountryId() == null ? null : countryService.findById(Long.parseLong(data.getCitizenshipCountryId()));

        data.getAddress().getCountryId();
        Optional.ofNullable(data.getUsername()).ifPresent(appUser::setUsername);
        Optional.ofNullable(data.getEmail()).ifPresent(appUser::setEmail);
        Optional.ofNullable(data.getPhone()).ifPresent(appUser::setPhone);
        Optional.ofNullable(birthDate).ifPresent(appUser::setBirthDate);
        Optional.ofNullable(gender).ifPresent(appUser::setGender);
        Optional.ofNullable(country).ifPresent(appUser::setCitizenship);
        Optional.ofNullable(address).ifPresent((Address a) -> {
            addressService.save(a);

            Optional.ofNullable(appUser.getResidency()).ifPresent(addressService::delete);
            appUser.setResidency(a);
        });
        Optional.ofNullable(passport).ifPresent((Passport p) -> {
            passportService.save(p);

            Optional.ofNullable(appUser.getPassport()).ifPresent(passportService::delete);
            appUser.setPassport(p);
        });

        return save(appUser);
    }
}
