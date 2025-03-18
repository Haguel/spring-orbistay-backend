package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.PassportDataRequestDTO;
import dev.haguel.orbistay.entity.Passport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

class PassportMapperIntegrationTest extends BaseMapperTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private PassportMapper passportMapper;

    @Nested
    class PassportDataRequestDTOMapping {
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
            assertEquals(Long.parseLong(dto.getCountryOfIssuanceId()), passport.getIssuingCountry().getId());
        }
    }
}