package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.AddressDataRequestDTO;
import dev.haguel.orbistay.dto.request.EditAppUserDataRequestDTO;
import dev.haguel.orbistay.dto.request.PassportDataRequestDTO;
import dev.haguel.orbistay.dto.response.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.service.AppUserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.SharedTestUtil;
import test_utils.TestDataGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class AppUserControllerTest {
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

    @Test
    void whenGetCurrentAppUser_thenReturnInfo() {
        String email = "john.doe@example.com";
        String password = "password123";
        JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInAndGetTokens(email, password, webTestClient);

        webTestClient.get()
                .uri("/app-user/get/current")
                .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetAppUserInfoResponseDTO.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(email, response.getEmail());
                });
    }

    @Test
    void whenEditAppUserWithFullData_thenReturnUpdatedUser() {
        String email = "john.doe@example.com";
        String password = "password123";
        JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInAndGetTokens(email, password, webTestClient);

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
                .uri("/app-user/edit/current")
                .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(editAppUserDataRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetAppUserInfoResponseDTO.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(editAppUserDataRequestDTO.getEmail(), response.getEmail());
                    assertEquals(editAppUserDataRequestDTO.getUsername(), response.getUsername());
                    assertEquals(editAppUserDataRequestDTO.getPhone(), response.getPhone());
                    assertEquals(editAppUserDataRequestDTO.getBirthDate(), response.getBirthDate().toString());
                    assertEquals(editAppUserDataRequestDTO.getGender(), response.getGender().toString());
                    assertEquals(editAppUserDataRequestDTO.getCitizenshipCountryId(), response.getCitizenship().getId().toString());
                    assertEquals(addressDataRequestDTO.getCity(), response.getResidency().getCity());
                    assertEquals(addressDataRequestDTO.getStreet(), response.getResidency().getStreet());
                });

        AppUser appUser = appUserService.findByEmail(editAppUserDataRequestDTO.getEmail());

        assertEquals(appUser.getEmail(), editAppUserDataRequestDTO.getEmail());
        assertEquals(appUser.getUsername(), editAppUserDataRequestDTO.getUsername());
        assertEquals(appUser.getPhone(), editAppUserDataRequestDTO.getPhone());
        assertEquals(appUser.getBirthDate().toString(), editAppUserDataRequestDTO.getBirthDate());
        assertEquals(appUser.getGender().toString(), editAppUserDataRequestDTO.getGender());
        assertEquals(appUser.getCitizenship().getId().toString(), editAppUserDataRequestDTO.getCitizenshipCountryId());
        assertEquals(appUser.getResidency().getCity(), addressDataRequestDTO.getCity());
        assertEquals(appUser.getResidency().getStreet(), addressDataRequestDTO.getStreet());
        assertEquals(appUser.getResidency().getCountry().getId().toString(), addressDataRequestDTO.getCountryId());
        assertEquals(appUser.getPassport().getPassportNumber(), passportDataRequestDTO.getPassportNumber());
        assertEquals(appUser.getPassport().getExpirationDate().toString(), passportDataRequestDTO.getExpirationDate());
        assertEquals(appUser.getPassport().getCountryOfIssuance().getId().toString(), passportDataRequestDTO.getCountryOfIssuanceId());
    }
}