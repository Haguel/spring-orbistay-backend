package dev.haguel.orbistay.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import dev.haguel.orbistay.dto.request.AddBankCardDTO;
import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.response.*;
import dev.haguel.orbistay.entity.*;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import dev.haguel.orbistay.exception.CountryNotFoundException;
import dev.haguel.orbistay.factory.MessageFactory;
import dev.haguel.orbistay.mapper.AddressMapper;
import dev.haguel.orbistay.mapper.AppUserMapper;
import dev.haguel.orbistay.mapper.BankCardMapper;
import dev.haguel.orbistay.mapper.PassportMapper;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.strategy.notification.context.NotificationContext;
import dev.haguel.orbistay.strategy.notification.context.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceUnitTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private BankCardService bankCardService;

    @Mock
    private AddressService addressService;

    @Mock
    private PassportService passportService;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private PassportMapper passportMapper;

    @Mock
    private CountryService countryService;

    @Mock
    private MessageFactory messageFactory;

    @Mock
    private BankCardMapper bankCardMapper;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private NotificationContext notificationContext;

    @InjectMocks
    private AppUserService appUserService;

    private BlobServiceClient mockedBlobServiceClient;

    @BeforeEach
    void setup() throws Exception {
        // Set @Value fields to dummy values
        Field containerNameField = AppUserService.class.getDeclaredField("containerName");
        containerNameField.setAccessible(true);
        containerNameField.set(appUserService, "test-container");

        Field connectionStringField = AppUserService.class.getDeclaredField("connectionString");
        connectionStringField.setAccessible(true);
        connectionStringField.set(appUserService, "dummy-connection-string");

        // Create and set a mocked BlobServiceClient
        mockedBlobServiceClient = mock(BlobServiceClient.class);
        Field blobServiceClientField = AppUserService.class.getDeclaredField("blobServiceClient");
        blobServiceClientField.setAccessible(true);
        blobServiceClientField.set(appUserService, mockedBlobServiceClient);

        // Note: We don’t call init() manually because @PostConstruct isn’t invoked by Mockito,
        // and we’re overriding blobServiceClient with our mock anyway.
    }

    @Nested
    class Save {
        @Test
        void whenSaveAppUser_thenReturnSavedAppUser() {
            AppUser appUser = new AppUser();
            when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

            AppUser result = appUserService.save(appUser);

            assertNotNull(result);
            assertEquals(appUser, result);
            verify(appUserRepository).save(appUser);
        }

        @Test
        void whenSaveAppUserWithDuplicateData_thenThrowDataIntegrityViolationException() {
            AppUser appUser = new AppUser();
            when(appUserRepository.save(any(AppUser.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

            assertThrows(DataIntegrityViolationException.class, () -> appUserService.save(appUser));
            verify(appUserRepository).save(appUser);
        }
    }

    @Nested
    class FindByEmail {
        @Test
        void whenFindByEmailWithExistingEmail_thenReturnAppUser() {
            String email = "test@example.com";
            AppUser appUser = new AppUser();
            when(appUserRepository.findAppUserByEmail(email)).thenReturn(Optional.of(appUser));

            AppUser result = appUserService.findByEmail(email);

            assertNotNull(result);
            assertEquals(appUser, result);
            verify(appUserRepository).findAppUserByEmail(email);
        }

        @Test
        void whenFindByEmailWithNonExistingEmail_thenThrowAppUserNotFoundException() {
            String email = "unknown@example.com";
            when(appUserRepository.findAppUserByEmail(email)).thenReturn(Optional.empty());

            assertThrows(AppUserNotFoundException.class, () -> appUserService.findByEmail(email));
            verify(appUserRepository).findAppUserByEmail(email);
        }
    }

    @Nested
    class FindById {
        @Test
        void whenFindByIdWithExistingId_thenReturnAppUser() {
            Long id = 1L;
            AppUser appUser = new AppUser();
            when(appUserRepository.findById(id)).thenReturn(Optional.of(appUser));

            AppUser result = appUserService.findById(id);

            assertNotNull(result);
            assertEquals(appUser, result);
            verify(appUserRepository).findById(id);
        }

        @Test
        void whenFindByIdWithNonExistingId_thenThrowAppUserNotFoundException() {
            Long id = 999L;
            when(appUserRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(AppUserNotFoundException.class, () -> appUserService.findById(id));
            verify(appUserRepository).findById(id);
        }
    }

    @Nested
    class EditAppUserData {
        @Test
        void whenEditAppUserDataWithValidData_thenUpdateAppUserAndReturnResponse() throws CountryNotFoundException {
            AppUser appUser = new AppUser();
            appUser.setEmail("old@example.com");
            EditAppUserDataRequestDTO request = new EditAppUserDataRequestDTO();
            request.setEmail("new@example.com");
            request.setCitizenshipCountryId("1");
            Country country = new Country();
            EmailVerification verification = new EmailVerification();
            String accessToken = "accessToken";
            String refreshToken = "refreshToken";
            GetAppUserInfoResponseDTO appUserInfoDTO = new GetAppUserInfoResponseDTO();

            when(countryService.findById(1L)).thenReturn(country);
            when(emailVerificationService.createNeededVerificationForAppUser(appUser)).thenReturn(verification);
            when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);
            when(jwtService.generateAccessToken(appUser)).thenReturn(accessToken);
            when(jwtService.generateRefreshToken(appUser)).thenReturn(refreshToken);
            when(appUserMapper.appUserToAppUserInfoDTO(appUser)).thenReturn(appUserInfoDTO);
            when(notificationContext.getMessageFactory()).thenReturn(messageFactory);
            when(notificationContext.getMessageFactory().getVerificationMessage(any(AppUser.class))).thenReturn("message");

            EditAppUserInfoResponseWrapperDTO result = appUserService.editAppUserData(appUser, request);

            assertNotNull(result);
            assertEquals(accessToken, result.getJwtDTO().getAccessToken());
            assertEquals(refreshToken, result.getJwtDTO().getRefreshToken());
            assertEquals(appUserInfoDTO, result.getEditAppUserInfoResponseDTO().getAppUser());
            verify(appUserRepository, times(2)).save(appUser);
            verify(emailVerificationService).createNeededVerificationForAppUser(appUser);
            verify(notificationContext).setNotificationType(NotificationType.EMAIL);
            verify(notificationContext).notifyUser(eq("new@example.com"), eq("Orbistay Email Verification"), eq("message"));
            verify(redisService).setAuthValue("new@example.com", refreshToken);
        }

        @Test
        void whenEditAppUserDataWithNullAppUser_thenThrowResponseStatusException() {
            EditAppUserDataRequestDTO request = new EditAppUserDataRequestDTO();

            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> appUserService.editAppUserData(null, request));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
            assertEquals("Provided null user", exception.getReason());
        }

        @Test
        void whenEditAppUserDataWithPartialData_thenUpdateOnlyProvidedFields() throws CountryNotFoundException {
            AppUser appUser = new AppUser();
            appUser.setUsername("oldUsername");
            appUser.setEmail("old@example.com");
            EditAppUserDataRequestDTO request = new EditAppUserDataRequestDTO();
            request.setUsername("newUsername");

            when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

            EditAppUserInfoResponseWrapperDTO result = appUserService.editAppUserData(appUser, request);

            assertEquals("newUsername", appUser.getUsername());
            assertEquals("old@example.com", appUser.getEmail());
            verify(appUserRepository).save(appUser);
            verify(jwtService).generateAccessToken(appUser);
            verify(jwtService).generateRefreshToken(appUser);
        }
    }

    @Nested
    class SetAvatar {
        @Test
        void whenSetAvatarWithValidFile_thenUploadAndSetAvatarUrl() throws IOException {
            // Arrange
            AppUser appUser = new AppUser();
            appUser.setId(1L);

            MultipartFile avatar = mock(MultipartFile.class);
            when(avatar.getInputStream()).thenReturn(mock(InputStream.class));
            when(avatar.getSize()).thenReturn(100L);
            when(avatar.getContentType()).thenReturn("image/jpeg");

            when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

            // Mock the BlobClient chain
            BlobContainerClient mockedContainerClient = mock(BlobContainerClient.class);
            BlobClient mockedBlobClient = mock(BlobClient.class);

            when(mockedBlobServiceClient.getBlobContainerClient("test-container"))
                    .thenReturn(mockedContainerClient);
            when(mockedContainerClient.getBlobClient("1"))
                    .thenReturn(mockedBlobClient);
            doNothing().when(mockedBlobClient).upload(any(InputStream.class), anyLong(), eq(true));
            doNothing().when(mockedBlobClient).setHttpHeaders(any(BlobHttpHeaders.class));
            when(mockedBlobClient.getBlobUrl()).thenReturn("http://mocked.url/avatar.jpg");

            // Act
            AppUser result = appUserService.setAvatar(appUser, avatar);

            // Assert
            assertEquals("http://mocked.url/avatar.jpg", result.getAvatarUrl());
            verify(appUserRepository).save(appUser);
            verify(mockedBlobClient).upload(any(InputStream.class), eq(100L), eq(true));
            verify(mockedBlobClient).setHttpHeaders(any(BlobHttpHeaders.class));
            verify(mockedBlobClient).getBlobUrl();
        }
    }

    @Nested
    class RemoveBankCard {
        @Test
        void whenRemoveBankCard_thenRemoveFromAppUserAndDelete() {
            AppUser appUser = new AppUser();
            BankCard bankCard = new BankCard();
            bankCard.setId(1L);
            appUser.setBankCards(new ArrayList<>());
            appUser.getBankCards().add(bankCard);
            when(bankCardService.findById(1L)).thenReturn(bankCard);
            when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

            AppUser result = appUserService.removeBankCard(1L, appUser);

            assertFalse(appUser.getBankCards().contains(bankCard));
            verify(appUserRepository).save(appUser);
            verify(bankCardService).delete(bankCard);
            assertEquals(appUser, result);
        }
    }
}