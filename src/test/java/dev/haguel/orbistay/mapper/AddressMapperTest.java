package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.AddressDataRequestDTO;
import dev.haguel.orbistay.entity.Address;
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
class AddressMapperTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private AddressMapper addressMapper;

    @Test
    void whenAddressDataRequestDTOToAddress_thenReturnAddress() {
        AddressDataRequestDTO dto = new AddressDataRequestDTO();
        dto.setCity("New York");
        dto.setCountryId("1");

        Address address = addressMapper.addressDataRequestDTOToAddress(dto);

        assertNotNull(address);
        assertEquals(dto.getCity(), address.getCity());
        assertEquals(Long.parseLong(dto.getCountryId()), address.getCountry().getId());
    }
}