package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.util.mapper.SharedMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class ReviewMapperTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Autowired
    private ReviewMapper reviewMapper;

    @Nested
    class WriteReviewRequestToReview {
        @Test
        void whenWriteReviewRequestToReviewWithValidData_thenReturnReview() {
            WriteReviewRequestDTO requestDTO = WriteReviewRequestDTO.builder()
                    .hotelId("1")
                    .content("Great hotel!")
                    .rate("8.6")
                    .goodSides("The staff was very friendly")
                    .badSides("The room was a bit small")
                    .build();

            Review review = reviewMapper.writeReviewRequestToReview(requestDTO);

            assertNotNull(review);
            assertEquals(Double.parseDouble(requestDTO.getRate()), review.getRate());
            assertEquals(requestDTO.getContent(), review.getContent());
            assertEquals(requestDTO.getGoodSides(), review.getGoodSides());
            assertEquals(requestDTO.getBadSides(), review.getBadSides());
        }
    }
}