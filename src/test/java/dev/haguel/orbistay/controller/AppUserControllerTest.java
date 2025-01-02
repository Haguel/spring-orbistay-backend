package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppUserControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @LocalServerPort
    int port;

    private final RestTemplate restTemplate;

    public AppUserControllerTest() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    private JwtResponseDTO signInAndGetTokens(String email, String password) {
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/auth/sign-in", HttpMethod.POST,
                new HttpEntity<>(signInRequestDTO), JwtResponseDTO.class);;

        return response.getBody();
    }

    @Test
    void whenGetCurrentAppUser_thenReturnInfo() {
        String email = "john.doe@example.com";
        JwtResponseDTO jwtResponseDTO = signInAndGetTokens(email, "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtResponseDTO.getAccessToken());
        HttpEntity<JwtAccessTokenDTO> entity = new HttpEntity<>( headers);

        ResponseEntity<GetAppUserInfoResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/app-user/get/current", HttpMethod.GET,
                entity, GetAppUserInfoResponseDTO.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getEmail(), email);
    }

}