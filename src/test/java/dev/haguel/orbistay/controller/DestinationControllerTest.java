package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.response.GetPopularDestinationsResponseDTO;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

class DestinationControllerTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class GetPopularDestinations {
        @Test
        void whenGetPopularDestinations_thenReturnPopularDestinations() {
            webTestClient.get()
                    .uri(EndPoints.Destinations.GET_POPULAR_DESTINATIONS)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetPopularDestinationsResponseDTO.class)
                    .value(popularDestinations -> {
                        assertNotNull(popularDestinations);
                        assertFalse(popularDestinations.isEmpty());
                        assertEquals("US", popularDestinations.get(0).getCountry().getCode());
                    });
        }
    }
}