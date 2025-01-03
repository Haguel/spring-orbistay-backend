package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.GetAppUserInfoResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class AppUserMapperTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserMapper appUserMapper;

    @Test
    void whenAppUserToAppUserInfoDTO_thenReturnGetAppUserInfoResponseDTO() {
        AppUser appUser = appUserRepository.findById(1L).orElse(null);
        GetAppUserInfoResponseDTO responseDTO = appUserMapper.appUserToAppUserInfoDTO(appUser);

        assertNotNull(responseDTO);
        assertEquals(appUser.getId(), responseDTO.getId());
        assertEquals(appUser.getEmail(), responseDTO.getEmail());
        assertEquals(appUser.getPhone(), responseDTO.getPhone());
        assertEquals(appUser.getBirthDate(), responseDTO.getBirthDate());
        assertEquals(appUser.getGender().name(), responseDTO.getGender().name());
        assertEquals(appUser.getUsername(), responseDTO.getUsername());
        assertEquals(appUser.getAvatarUrl(), responseDTO.getAvatarUrl());
        assertEquals(appUser.getId(), responseDTO.getId());
        assertEquals(appUser.getCitizenship().getId(), responseDTO.getCitizenship().getId());
        assertEquals(appUser.getResidency().getId(), responseDTO.getResidency().getId());
    }

    @Test
    void whenAppUserIsNull_thenReturnNull() {
        GetAppUserInfoResponseDTO responseDTO = appUserMapper.appUserToAppUserInfoDTO(null);

        assertNull(responseDTO);
    }
}