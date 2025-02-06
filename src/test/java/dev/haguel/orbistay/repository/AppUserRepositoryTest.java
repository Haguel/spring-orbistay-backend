package dev.haguel.orbistay.repository;

import dev.haguel.orbistay.entity.AppUser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppUserRepositoryTest extends BaseRepositoryTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private AppUserRepository appUserRepository;

    @Nested
    class FindByEmail {
        @Test
        void whenFindByEmail_thenReturnUser() {
            String expected = "jane.smith@example.com";
            AppUser result = appUserRepository.findAppUserByEmail(expected).orElse(null);
            assertNotNull(result);

            String actual = result.getEmail();
            assertEquals(expected, actual);
        }
    }
}