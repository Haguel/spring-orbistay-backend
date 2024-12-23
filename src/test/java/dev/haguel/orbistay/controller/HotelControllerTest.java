package dev.haguel.orbistay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.haguel.orbistay.dto.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.GetHotelsRequestDTO;
import dev.haguel.orbistay.dto.GetHotelsResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @LocalServerPort
    int port;

    private final RestTemplate restTemplate;

    public HotelControllerTest() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
//        messageConverters.add(converter);
//        restTemplate.setMessageConverters(messageConverters);
    }

    @Test
    void whenGetHotelsWithValidCriteria_thenReturnHotels() {
        // Case 1: Valid name
        GetHotelsRequestDTO requestDTO1 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        ResponseEntity<GetHotelsResponseDTO[]> response1 = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                new HttpEntity<>(requestDTO1), GetHotelsResponseDTO[].class);
        assertEquals(200, response1.getStatusCode().value());
        assertNotNull(response1.getBody());
        assertTrue(response1.getBody().length > 0);

        // Case 2: Valid city
        GetHotelsRequestDTO requestDTO2 = GetHotelsRequestDTO.builder()
                .name(null)
                .city("New York")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        ResponseEntity<GetHotelsResponseDTO[]> response2 = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                new HttpEntity<>(requestDTO2), GetHotelsResponseDTO[].class);
        assertEquals(200, response2.getStatusCode().value());
        assertNotNull(response2.getBody());
        assertTrue(response2.getBody().length > 0);

        // Case 3: Valid country
        GetHotelsRequestDTO requestDTO3 = GetHotelsRequestDTO.builder()
                .name(null)
                .city(null)
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        ResponseEntity<GetHotelsResponseDTO[]> response3 = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                new HttpEntity<>(requestDTO3), GetHotelsResponseDTO[].class);
        assertEquals(200, response3.getStatusCode().value());
        assertNotNull(response3.getBody());
        assertTrue(response3.getBody().length > 0);

        // Case 4: Valid people count
        GetHotelsRequestDTO requestDTO4 = GetHotelsRequestDTO.builder()
                .name(null)
                .city(null)
                .country(null)
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        ResponseEntity<GetHotelsResponseDTO[]> response4 = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                new HttpEntity<>(requestDTO4), GetHotelsResponseDTO[].class);
        assertEquals(200, response4.getStatusCode().value());
        assertNotNull(response4.getBody());
        assertTrue(response4.getBody().length > 0);

        // Case 5: Valid children friendly
        GetHotelsRequestDTO requestDTO5 = GetHotelsRequestDTO.builder()
                .name(null)
                .city(null)
                .country(null)
                .peopleCount(null)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        ResponseEntity<GetHotelsResponseDTO[]> response5 = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                new HttpEntity<>(requestDTO5), GetHotelsResponseDTO[].class);
        assertEquals(200, response5.getStatusCode().value());
        assertNotNull(response5.getBody());
        assertTrue(response5.getBody().length > 0);

        // Case 6: Valid check-in and check-out dates
        GetHotelsRequestDTO requestDTO6 = GetHotelsRequestDTO.builder()
                .name(null)
                .city(null)
                .country(null)
                .peopleCount(null)
                .isChildrenFriendly(null)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        ResponseEntity<GetHotelsResponseDTO[]> response6 = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                new HttpEntity<>(requestDTO6), GetHotelsResponseDTO[].class);
        assertEquals(200, response6.getStatusCode().value());
        assertNotNull(response6.getBody());
        assertTrue(response6.getBody().length > 0);
    }

    @Test
    void whenGetHotelsWithInvalidCriteria_thenReturnError() {
        // Case 1: Invalid name
        GetHotelsRequestDTO requestDTO1 = GetHotelsRequestDTO.builder()
                .name("Nonexistent Hotel")
                .city("New York")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO1), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 2: Invalid city
        GetHotelsRequestDTO requestDTO2 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("Nonexistent City")
                .country("United States")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO2), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 3: Invalid country
        GetHotelsRequestDTO requestDTO3 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("Nonexistent Country")
                .peopleCount(2)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO3), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

        // Case 4: Invalid people count
        GetHotelsRequestDTO requestDTO4 = GetHotelsRequestDTO.builder()
                .name("Hotel New York 1")
                .city("New York")
                .country("United States")
                .peopleCount(10)
                .isChildrenFriendly(true)
                .checkIn(LocalDate.of(2024, 1, 1))
                .checkOut(LocalDate.of(2024, 1, 10))
                .build();

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/filter", HttpMethod.GET,
                    new HttpEntity<>(requestDTO4), GetHotelsResponseDTO[].class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Test
    void whenGetHotelWithValidId_thenReturnHotel() {
        Long validHotelId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GetHotelResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/hotel/get/" + validHotelId, HttpMethod.GET,
                entity, GetHotelResponseDTO.class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(validHotelId, response.getBody().getId());
    }

    @Test
    void whenGetHotelWithInvalidId_thenReturnError() {
        Long invalidHotelId = 999L;

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/hotel/get/" + invalidHotelId, HttpMethod.GET, HttpEntity.EMPTY, GetHotelResponseDTO.class);
            fail("Should have thrown 404 exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }
}