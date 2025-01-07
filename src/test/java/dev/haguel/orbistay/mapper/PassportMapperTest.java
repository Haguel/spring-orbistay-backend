package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.PassportDataRequestDTO;
import dev.haguel.orbistay.entity.Passport;
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
class PassportMapperTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private PassportMapper passportMapper;

    @Test
    void whenPassportDataRequestDTOToPassport_thenReturnPassport() {
        PassportDataRequestDTO dto = new PassportDataRequestDTO();
        dto.setPassportNumber("A12345678");
        dto.setExpirationDate("2030-01-01");
        dto.setCountryOfIssuanceId("1");

        Passport passport = passportMapper.passportDataRequestDTOToPassport(dto);

        assertNotNull(passport);
        assertEquals(dto.getPassportNumber(), passport.getPassportNumber());
        assertEquals(dto.getExpirationDate(), passport.getExpirationDate().toString());
        assertEquals(Long.parseLong(dto.getCountryOfIssuanceId()), passport.getCountryOfIssuance().getId());
    }

}