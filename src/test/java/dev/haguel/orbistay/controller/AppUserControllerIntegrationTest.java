package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.AddBankCardDTO;
import dev.haguel.orbistay.dto.request.AddressDataRequestDTO;
import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.request.PassportDataRequestDTO;
import dev.haguel.orbistay.dto.response.EditAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;
import test_utils.TestDataGenerator;
import test_utils.TestDataStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AppUserControllerIntegrationTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

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
                    .expirationDate(LocalDate.now().plusYears(1).toString())
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
                        assertEquals(passportDataRequestDTO.getCountryOfIssuanceId(), response.getAppUser().getPassport().getIssuingCountry().getId().toString());
                    });
        }
    }

    @Nested
    class AddBankCard {
        @Test
        void whenAddBankCard_thenReturnAppUser() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            AddBankCardDTO addBankCardDTO = AddBankCardDTO.builder()
                    .cardNumber("1234567891234567")
                    .cardHolderName("John Doe")
                    .expirationDate("12/25")
                    .cvv("123")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.AppUsers.ADD_BANK_CARD)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .bodyValue(addBankCardDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetAppUserInfoResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertEquals(2, response.getBankCards().size());
                    });
        }
    }

    @Nested
    class RemoveBankCard {
        @Test
        void whenRemoveBankCard_thenReturnAppUser() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.AppUsers.REMOVE_BANK_CARD + "/1")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetAppUserInfoResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertEquals(0, response.getBankCards().size());
                    });
        }
    }
}