package dev.haguel.orbistay.controller;

import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;

class FavoritesControllerTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class AddHotelToFavorites {
        @Test
        void whenAddHotelToFavorites_thenReturnHotelAddedToFavorites() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.post()
                    .uri(EndPoints.Favorites.ADD_TO_FAVORITES + "/1")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isCreated();

            webTestClient.post()
                    .uri(EndPoints.Favorites.ADD_TO_FAVORITES + "/1")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isCreated();

            webTestClient.post()
                    .uri(EndPoints.Favorites.ADD_TO_FAVORITES + "/2")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isCreated();
        }

        @Test
        void whenAddHotelToFavoritesWithInvalidHotelId_thenReturns404() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.post()
                    .uri(EndPoints.Favorites.ADD_TO_FAVORITES + "/999")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetFavorites {
        @Test
        void whenGetFavorites_thenReturnFavorites() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.get()
                    .uri(EndPoints.Favorites.GET_FAVORITES)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetHotelsResponseDTO.class)
                    .hasSize(3);
        }
    }

    @Nested
    class RemoveFavorites {
        @Test
        void whenRemoveFavorites_thenReturnFavoritesRemoved() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Favorites.REMOVE_FAVORITES + "/1")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();

            webTestClient.delete()
                    .uri(EndPoints.Favorites.REMOVE_FAVORITES + "/2")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenRemoveFavoritesWithInvalidHotelId_thenReturns404() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Favorites.REMOVE_FAVORITES + "/999")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void whenRemoveFavoritesWithInvalidFavoritesId_thenReturns404() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Favorites.REMOVE_FAVORITES + "/10")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}