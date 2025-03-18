package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.response.GetDestinationsResponseDTO;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

class DestinationControllerIntegrationTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Nested
    class GetPopularDestinations {
        @Test
        void whenGetPopularDestinations_thenReturnPopularDestinations() {
            webTestClient.get()
                    .uri(EndPoints.Destinations.GET_POPULAR_DESTINATIONS)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetDestinationsResponseDTO.class)
                    .value(popularDestinations -> {
                        assertNotNull(popularDestinations);
                        assertFalse(popularDestinations.isEmpty());
                        assertEquals("US", popularDestinations.get(0).getCountry().getCode());
                    });
        }
    }

    @Nested
    class GetDestinationsSimilarToText {
        @Test
        void whenGetDestinationsSimilarToText_thenReturnSimilarDestinations() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Destinations.GET_DESTINATIONS_SIMILAR_TO_TEXT)
                            .queryParam("text", "New ")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetDestinationsResponseDTO.class)
                    .value(similarDestinations -> {
                        assertNotNull(similarDestinations);
                        assertFalse(similarDestinations.isEmpty());
                        assertEquals(1, similarDestinations.size());
                        assertEquals("New York", similarDestinations.get(0).getCity());
                    });

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Destinations.GET_DESTINATIONS_SIMILAR_TO_TEXT)
                            .queryParam("text", "Zurich")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetDestinationsResponseDTO.class)
                    .value(similarDestinations -> {
                        assertNotNull(similarDestinations);
                        assertFalse(similarDestinations.isEmpty());
                        assertEquals(1, similarDestinations.size());
                        assertEquals("Zurich", similarDestinations.get(0).getCity());
                    });
        }
    }
}