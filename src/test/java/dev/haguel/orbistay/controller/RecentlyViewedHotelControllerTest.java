package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.util.EndPoints;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import test_utils.SharedTestUtil;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecentlyViewedHotelControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class AddToRecentlyViewedHotels {
        @Test
        void whenAddToRecentlyViewedHotels_thenReturns200() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.post()
                    .uri(EndPoints.RecentlyViewedHotels.ADD_TO_RECENTLY_VIEWED_HOTELS + "/1")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();

            webTestClient.post()
                    .uri(EndPoints.RecentlyViewedHotels.ADD_TO_RECENTLY_VIEWED_HOTELS + "/1")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();

            webTestClient.post()
                    .uri(EndPoints.RecentlyViewedHotels.ADD_TO_RECENTLY_VIEWED_HOTELS + "/2")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenAddToRecentlyViewedHotelsWithInvalidHotelId_thenReturns404() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.post()
                    .uri(EndPoints.RecentlyViewedHotels.ADD_TO_RECENTLY_VIEWED_HOTELS + "/-1")
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetRecentlyViewedHotels {
        @Test
        void whenGetRecentlyViewedHotels_thenReturns200() {
            JwtResponseDTO jwtResponseDTO = SharedTestUtil.signInJohnDoeAndGetTokens(webTestClient);

            webTestClient.get()
                    .uri(EndPoints.RecentlyViewedHotels.GET_RECENTLY_VIEWED_HOTELS)
                    .header("Authorization", "Bearer " + jwtResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetHotelsResponseDTO.class)
                    .hasSize(3);
        }
    }
}