package dev.haguel.orbistay.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import dev.haguel.orbistay.dto.request.AddBankCardDTO;
import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.dto.response.EditAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.EditAppUserInfoResponseWrapperDTO;
import dev.haguel.orbistay.dto.response.JwtDTO;
import dev.haguel.orbistay.entity.*;
import dev.haguel.orbistay.entity.enumeration.Gender;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.mapper.AddressMapper;
import dev.haguel.orbistay.mapper.AppUserMapper;
import dev.haguel.orbistay.mapper.BankCardMapper;
import dev.haguel.orbistay.mapper.PassportMapper;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.strategy.notification.context.NotificationContext;
import dev.haguel.orbistay.strategy.notification.context.NotificationType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final BankCardService bankCardService;
    private final CountryService countryService;
    private final AddressService addressService;
    private final PassportService passportService;
    private final AddressMapper addressMapper;
    private final PassportMapper passportMapper;
    private final BankCardMapper bankCardMapper;
    private final AppUserMapper appUserMapper;
    private final RedisService redisService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final NotificationContext notificationContext;
    private BlobServiceClient blobServiceClient;

    @Value("${spring.cloud.azure.storage.blob.account-name}")
    private String containerName;
    @Value("${azure.blob-storage.connection-string}")
    private String connectionString;

    @PostConstruct
    public void init() {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    public AppUser save(AppUser appUser)
            throws DataIntegrityViolationException {
        appUser = appUserRepository.save(appUser);

        log.info("User saved to database");
        return appUser;
    }

    private JwtDTO getJwtResponseDTO(AppUser appUser) {
        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        redisService.setAuthValue(appUser.getEmail(), refreshToken);

        return new JwtDTO(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AppUser findByEmail(String email)
            throws AppUserNotFoundException {
        AppUser appUser = appUserRepository.findAppUserByEmail(email).orElse(null);

        if(appUser == null) {
            log.warn("User couldn't be found in database by provided email");
            throw new AppUserNotFoundException("User with provided email not found in database");
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
    public EditAppUserInfoResponseWrapperDTO editAppUserData(AppUser appUser, EditAppUserDataRequestDTO data)
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
                .map(passportMapper::passportDataRequestDTOToPassport).orElse(null);
        Country country = data.getCitizenshipCountryId() == null ? null : countryService.findById(Long.parseLong(data.getCitizenshipCountryId()));

        Optional.ofNullable(data.getUsername()).ifPresent(appUser::setUsername);
        Optional.ofNullable(data.getPhone()).ifPresent(appUser::setPhone);
        Optional.ofNullable(birthDate).ifPresent(appUser::setBirthDate);
        Optional.ofNullable(gender).ifPresent(appUser::setGender);
        Optional.ofNullable(country).ifPresent(appUser::setCitizenship);

        if(data.getEmail() != null && !data.getEmail().equals(appUser.getEmail())) {
            if(appUser.getEmailVerification() != null) {
                emailService.delete(appUser.getEmailVerification());
            }

            appUser.setEmailVerification(emailService.createNeededVerificationForAppUser(appUser));
            appUser.setEmail(data.getEmail());
            appUser = save(appUser);

            notificationContext.setNotificationType(NotificationType.EMAIL);
            String message = notificationContext.getMessageFactory().getVerificationMessage(appUser);
            notificationContext.notifyUser(appUser.getEmail(), "Orbistay Email Verification", message);
        }
        if(address != null) {
            addressService.save(address);

            Optional.ofNullable(appUser.getResidency()).ifPresent(addressService::delete);
            appUser.setResidency(address);
        }
        if(passport != null) {
            passport.setAppUser(appUser);
            passportService.save(passport);

            Optional.ofNullable(appUser.getPassport()).ifPresent(passportService::delete);
            appUser.setPassport(passport);
        }

        AppUser saved = save(appUser);

        JwtDTO jwtDTO = getJwtResponseDTO(saved);
        EditAppUserInfoResponseDTO editAppUserInfoResponseDTO = EditAppUserInfoResponseDTO.builder()
                .appUser(appUserMapper.appUserToAppUserInfoDTO(saved))
                .accessTokenResponseDTO(new AccessTokenResponseDTO(jwtDTO.getAccessToken()))
                .build();
        return EditAppUserInfoResponseWrapperDTO.builder()
                .editAppUserInfoResponseDTO(editAppUserInfoResponseDTO)
                .jwtDTO(jwtDTO)
                .build();
    }

    public AppUser setAvatar(AppUser appUser, MultipartFile avatar) throws IOException {
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName)
                .getBlobClient(String.valueOf(appUser.getId()));

        try {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(avatar.getContentType());
            blobClient.upload(avatar.getInputStream(), avatar.getSize(), true);
            blobClient.setHttpHeaders(headers);
        } catch (IOException exception) {
            log.error("Error while uploading avatar to blob storage");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while uploading avatar to blob storage");
        }

        String avatarUrl = blobClient.getBlobUrl();

        appUser.setAvatarUrl(avatarUrl);
        appUser = save(appUser);

        return appUser;
    }

    public AppUser addBankCard(AddBankCardDTO addBankCardDTO, AppUser appUser) {
        BankCard bankCard = bankCardMapper.addBankCardDTOToBankCard(addBankCardDTO);
        bankCard.setAppUser(appUser);
        bankCardService.save(bankCard);
        log.info("size {}", appUser.getBankCards().size());

        return appUser;
    }

    public AppUser removeBankCard(Long bankCardId, AppUser appUser) {
        BankCard bankCard = bankCardService.findById(bankCardId);

        appUser.getBankCards().remove(bankCard);
        appUser = save(appUser);

        bankCardService.delete(bankCard);

        return appUser;
    }
}
