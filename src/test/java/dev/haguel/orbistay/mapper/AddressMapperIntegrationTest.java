package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.AddressDataRequestDTO;
import dev.haguel.orbistay.entity.Address;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

class AddressMapperIntegrationTest extends BaseMapperTestClass{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private AddressMapper addressMapper;

    @Nested
    class AddressDataRequestDTOMapping {
        @Test
        void whenAddressDataRequestDTOToAddress_thenReturnAddress() {
            AddressDataRequestDTO dto = new AddressDataRequestDTO();
            dto.setCity("New York");
            dto.setCountryId("1");

            Address address = addressMapper.addressDataRequestDTOToAddress(dto);

            assertNotNull(address);
            assertNotNull(address.getCountry().getId());
            assertEquals(dto.getCity(), address.getCity());
            assertEquals(Long.parseLong(dto.getCountryId()), address.getCountry().getId());
        }
    }
}