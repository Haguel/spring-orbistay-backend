package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.AddressDataRequestDTO;
import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.request.PassportDataRequestDTO;
import dev.haguel.orbistay.dto.request.SignInRequestDTO;
import dev.haguel.orbistay.dto.response.EditAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.service.AppUserService;
import dev.haguel.orbistay.service.RedisService;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;
import test_utils.TestDataGenerator;
import test_utils.TestDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class AppUserControllerTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class GetCurrentAppUser {
        @Test
        void whenGetCurrentAppUser_thenReturnInfo() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.get()
                    .uri(EndPoints.AppUsers.GET_CURRENT_APP_USER)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetAppUserInfoResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertEquals(TestDataStorage.JOHN_DOE_EMAIL, response.getEmail());
                    });
        }
    }

    @Nested
    class EditCurrentAppUser {
        @Test
        void whenEditAppUserWithFullData_thenReturnUpdatedUser() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            AddressDataRequestDTO addressDataRequestDTO = AddressDataRequestDTO.builder()
                    .countryId(TestDataGenerator.generateRandomCountryId())
                    .city(TestDataGenerator.generateRandomCity())
                    .street(TestDataGenerator.generateRandomStreet())
                    .countryId(TestDataGenerator.generateRandomCountryId())
                    .build();
            PassportDataRequestDTO passportDataRequestDTO = PassportDataRequestDTO.builder()
                    .firstName(TestDataGenerator.generateRandomFirstName())
                    .lastName(TestDataGenerator.generateRandomLastName())
                    .passportNumber(TestDataGenerator.generateRandomPassportNumber())
                    .countryOfIssuanceId(TestDataGenerator.generateRandomCountryId())
                    .expirationDate(TestDataGenerator.generateRandomExpirationDate(false))
                    .countryOfIssuanceId(TestDataGenerator.generateRandomCountryId())
                    .build();
            EditAppUserDataRequestDTO editAppUserDataRequestDTO = EditAppUserDataRequestDTO.builder()
                    .email(TestDataGenerator.generateRandomEmail())
                    .username(TestDataGenerator.generateRandomUsername())
                    .phone(TestDataGenerator.generateRandomPhoneNumber())
                    .birthDate(TestDataGenerator.generateRandomStringDate())
                    .gender(TestDataGenerator.generateRandomGender())
                    .citizenshipCountryId(TestDataGenerator.generateRandomCountryId())
                    .address(addressDataRequestDTO)
                    .passport(passportDataRequestDTO)
                    .build();

            webTestClient.put()
                    .uri(EndPoints.AppUsers.EDIT_CURRENT_APP_USER)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(editAppUserDataRequestDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().exists(HttpHeaders.SET_COOKIE)
                    .expectHeader().valueMatches(HttpHeaders.SET_COOKIE, "refresh_token=.*; Path=/; Max-Age=\\d+; Expires=.*; HttpOnly; SameSite=Lax")
                    .expectBody(EditAppUserInfoResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertEquals(editAppUserDataRequestDTO.getEmail(), response.getAppUser().getEmail());
                        assertEquals(editAppUserDataRequestDTO.getUsername(), response.getAppUser().getUsername());
                        assertEquals(editAppUserDataRequestDTO.getPhone(), response.getAppUser().getPhone());
                        assertEquals(editAppUserDataRequestDTO.getBirthDate(), response.getAppUser().getBirthDate().toString());
                        assertEquals(editAppUserDataRequestDTO.getGender(), response.getAppUser().getGender().toString());
                        assertEquals(editAppUserDataRequestDTO.getCitizenshipCountryId(), response.getAppUser().getCitizenship().getId().toString());
                        assertFalse(response.getAppUser().getEmailVerification().isVerified());
                        assertEquals(addressDataRequestDTO.getCity(), response.getAppUser().getResidency().getCity());
                        assertEquals(addressDataRequestDTO.getStreet(), response.getAppUser().getResidency().getStreet());
                        assertEquals(addressDataRequestDTO.getCountryId(), response.getAppUser().getResidency().getCountry().getId().toString());
                        assertEquals(passportDataRequestDTO.getPassportNumber(), response.getAppUser().getPassport().getPassportNumber());
                        assertEquals(passportDataRequestDTO.getExpirationDate(), response.getAppUser().getPassport().getExpirationDate().toString());
                        assertEquals(passportDataRequestDTO.getCountryOfIssuanceId(), response.getAppUser().getPassport().getCountryOfIssuance().getId().toString());
                    });
        }
    }
}