package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.Favorites;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.Assert.*;

public class FavoritesRepositoryIntegrationTest extends BaseRepositoryTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private FavoritesRepository repository;

    @Nested
    class FindByHotelIdAndUserId {
        @Test
        void whenFindByHotelIdAndUserId_thenReturnFavorite() {
            Favorites favorites = repository.findFavoritesByHotelIdAndAppUserId(1L, 1L).orElse(null);

            assertNotNull(favorites);
            assertEquals(favorites.getAppUser().getId().longValue(), 1L);
            assertEquals(favorites.getHotel().getId().longValue(), 1L);
        }

        @Test
        void whenFindByHotelIdAndUserIdWithInvalidHotelId_thenReturnNull() {
            Favorites favorites = repository.findFavoritesByHotelIdAndAppUserId(999L, 1L).orElse(null);

            assertNull(favorites);
        }
    }

}
