package dev.haguel.orbistay.controller;

import com.google.common.collect.Lists;
import com.redis.testcontainers.RedisContainer;
import dev.haguel.orbistay.dto.request.HotelFiltersDTO;
import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.dto.request.enumeration.HotelStars;
import dev.haguel.orbistay.dto.request.enumeration.ObjectValuation;
import dev.haguel.orbistay.dto.response.GetFilteredHotelsResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.request.GetFilteredHotelsRequestDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.util.EndPoints;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import test_utils.SharedTestUtil;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HotelControllerIntegrationTest extends BaseControllerTestClass {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.0-alpine");

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer("redis:6.2-alpine");

    @Nested
    class GetFilteredHotels {
        @Test
        void whenGetFilteredHotelsWithValidCriteria_thenReturnHotels() {
            HotelFiltersDTO hotelFiltersDTO = HotelFiltersDTO.builder()
                    .minPrice("5.0")
                    .maxPrice("25.0")
                    .stars(Lists.newArrayList(HotelStars.THREE_STARS, HotelStars.FOUR_STARS, HotelStars.FIVE_STARS))
                    .valuations(Lists.newArrayList(ObjectValuation.EXCELLENT))
                    .build();
            GetFilteredHotelsRequestDTO requestDTO = GetFilteredHotelsRequestDTO.builder()
                    .name("Hotel New York 1")
                    .city("New York")
                    .countryId("1")
                    .peopleCount(String.valueOf(2))
                    .isChildrenFriendly(String.valueOf(true))
                    .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                    .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                    .filters(hotelFiltersDTO)
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTELS)
                            .queryParam("name", requestDTO.getName())
                            .queryParam("city", requestDTO.getCity())
                            .queryParam("countryId", requestDTO.getCountryId())
                            .queryParam("peopleCount", requestDTO.getPeopleCount())
                            .queryParam("isChildrenFriendly", requestDTO.getIsChildrenFriendly())
                            .queryParam("checkIn", requestDTO.getCheckIn())
                            .queryParam("checkOut", requestDTO.getCheckOut())
                            .queryParam("minPrice", requestDTO.getFilters().getMinPrice())
                            .queryParam("maxPrice", requestDTO.getFilters().getMaxPrice())
                            .queryParam("stars", String.join(",",
                                    requestDTO.getFilters().getStars().stream().map(Enum::name).toList()))
                            .queryParam("valuations", String.join(",",
                                    requestDTO.getFilters().getValuations().stream().map(Enum::name).toList()))
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetFilteredHotelsResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response.get(0).getHotels());
                        assertFalse(response.get(0).getHotels().isEmpty());
                    });
        }

        @Test
        void whenGetFilteredHotels_thenReturnValidFiltersCount() {
            HotelFiltersDTO hotelFiltersDTO = HotelFiltersDTO.builder()
                    .stars(Lists.newArrayList(HotelStars.FOUR_STARS, HotelStars.FIVE_STARS))
                    .build();
            GetFilteredHotelsRequestDTO requestDTO = GetFilteredHotelsRequestDTO.builder()
                    .filters(hotelFiltersDTO)
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTELS)
                            .queryParam("stars", String.join(",",
                                    requestDTO.getFilters().getStars().stream().map(Enum::name).toList()))
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetFilteredHotelsResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertFalse(response.get(0).getHotels().isEmpty());
                        assertEquals(4, response.get(0).getHotels().size());
                    });

            hotelFiltersDTO = HotelFiltersDTO.builder()
                    .valuations(Lists.newArrayList(ObjectValuation.EXCELLENT))
                    .build();
            GetFilteredHotelsRequestDTO requestDTO2 = GetFilteredHotelsRequestDTO.builder()
                    .filters(hotelFiltersDTO)
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTELS)
                            .queryParam("valuations", String.join(",",
                                    requestDTO2.getFilters().getValuations().stream().map(Enum::name).toList()))
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetFilteredHotelsResponseDTO.class)
                    .value(response -> {
                        assertNotNull(response);
                        assertFalse(response.get(0).getHotels().isEmpty());
                        assertEquals(2, response.get(0).getHotels().size());
                    });
        }

        @Test
        void whenGetFilteredHotelsWithInvalidCriteria_thenReturn404() {
            GetFilteredHotelsRequestDTO requestDTO = GetFilteredHotelsRequestDTO.builder()
                    .name("Invalid Hotel")
                    .city("Invalid City")
                    .countryId("-1")
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTELS)
                            .queryParam("name", requestDTO.getName())
                            .queryParam("city", requestDTO.getCity())
                            .queryParam("countryId", requestDTO.getCountryId())
                            .build())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetHotel {
        @Test
        void whenGetHotelWithValidId_thenReturnHotel() {
            Long validHotelId = 1L;

            webTestClient.get()
                    .uri(EndPoints.Hotels.GET_HOTEL + "/" + validHotelId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetHotelResponseDTO.class)
                    .value(hotel -> {
                        assertNotNull(hotel);
                        assertEquals(validHotelId, hotel.getId());
                    });
        }

        @Test
        void whenGetHotelWithInvalidId_thenReturn404() {
            Long invalidHotelId = 999L;

            webTestClient.mutate()
                    .build()
                    .get()
                    .uri(EndPoints.Hotels.GET_HOTEL + "/" + invalidHotelId)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetFilteredHotelRooms {
        @Test
        void whenGetFilteredHotelRoomsWithValidCriteria_thenReturnHotelRooms() {
            GetFileredHotelRoomsRequestDTO requestDTO = GetFileredHotelRoomsRequestDTO.builder()
                    .hotelId(String.valueOf(1L))
                    .peopleCount(String.valueOf(2))
                    .isChildrenFriendly(String.valueOf(true))
                    .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                    .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                    .minPrice(String.valueOf(5.0))
                    .maxPrice(String.valueOf(25.0))
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTEL_ROOMS)
                            .queryParam("hotelId", requestDTO.getHotelId())
                            .queryParam("peopleCount", requestDTO.getPeopleCount())
                            .queryParam("isChildrenFriendly", requestDTO.getIsChildrenFriendly())
                            .queryParam("checkIn", requestDTO.getCheckIn())
                            .queryParam("checkOut", requestDTO.getCheckOut())
                            .queryParam("minPrice", requestDTO.getMinPrice())
                            .queryParam("maxPrice", requestDTO.getMaxPrice())
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(HotelRoom.class)
                    .value(hotelRooms -> {
                        assertNotNull(hotelRooms);
                        assertFalse(hotelRooms.isEmpty());
                    });
        }

        @Test
        void whenGetFilteredHotelRoomsWithInvalidCriteria_thenReturn404() {
            GetFileredHotelRoomsRequestDTO requestDTO = GetFileredHotelRoomsRequestDTO.builder()
                    .hotelId(String.valueOf(999L))
                    .peopleCount(String.valueOf(2))
                    .isChildrenFriendly(String.valueOf(true))
                    .checkIn(String.valueOf(LocalDate.of(2024, 1, 1)))
                    .checkOut(String.valueOf(LocalDate.of(2024, 1, 10)))
                    .minPrice(String.valueOf(5.0))
                    .maxPrice(String.valueOf(25.0))
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTEL_ROOMS)
                            .queryParam("hotelId", requestDTO.getHotelId())
                            .queryParam("peopleCount", requestDTO.getPeopleCount())
                            .queryParam("isChildrenFriendly", requestDTO.getIsChildrenFriendly())
                            .queryParam("checkIn", requestDTO.getCheckIn())
                            .queryParam("checkOut", requestDTO.getCheckOut())
                            .queryParam("minPrice", requestDTO.getMinPrice())
                            .queryParam("maxPrice", requestDTO.getMaxPrice())
                            .build())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void whenGetFilteredHotelRoomsWithTakenDates_thenReturn404() {
            GetFileredHotelRoomsRequestDTO requestDTO = GetFileredHotelRoomsRequestDTO.builder()
                    .hotelId(String.valueOf(1L))
                    .peopleCount(String.valueOf(2))
                    .isChildrenFriendly(String.valueOf(true))
                    .checkIn(String.valueOf(LocalDate.of(2025, 11, 28)))
                    .checkOut(String.valueOf(LocalDate.of(2025, 12, 5)))
                    .minPrice(String.valueOf(5.0))
                    .maxPrice(String.valueOf(25.0))
                    .build();

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EndPoints.Hotels.GET_FILTERED_HOTEL_ROOMS)
                            .queryParam("hotelId", requestDTO.getHotelId())
                            .queryParam("peopleCount", requestDTO.getPeopleCount())
                            .queryParam("isChildrenFriendly", requestDTO.getIsChildrenFriendly())
                            .queryParam("checkIn", requestDTO.getCheckIn())
                            .queryParam("checkOut", requestDTO.getCheckOut())
                            .queryParam("minPrice", requestDTO.getMinPrice())
                            .queryParam("maxPrice", requestDTO.getMaxPrice())
                            .build())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetHotelRoom {
        @Test
        void whenGetHotelRoomWithValidId_thenReturnHotelRoom() {
            Long validHotelRoomId = 1L;

            webTestClient.get()
                    .uri(EndPoints.Hotels.GET_HOTEL_ROOM + "/" + validHotelRoomId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(HotelRoom.class)
                    .value(hotelRoom -> {
                        assertNotNull(hotelRoom);
                        assertEquals(validHotelRoomId, hotelRoom.getId());
                    });
        }

        @Test
        void whenGetHotelRoomWithInvalidId_thenReturn404() {
            Long invalidHotelRoomId = 999L;

            webTestClient.get()
                    .uri(EndPoints.Hotels.GET_HOTEL_ROOM + "/" + invalidHotelRoomId)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class WriteHotelReview {
        @Test
        void whenWriteReviewWithValidData_thenReturnReview() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            WriteReviewRequestDTO requestDTO = WriteReviewRequestDTO.builder()
                    .hotelId(String.valueOf(1L))
                    .content("Great hotel!")
                    .rate("8.6")
                    .pros("The staff was very friendly")
                    .cons("The room was a bit small")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Hotels.WRITE_HOTEL_REVIEW)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Review.class)
                    .value(review -> {
                        assertNotNull(review);
                        assertEquals(Long.parseLong(requestDTO.getHotelId()), review.getHotel().getId());
                        assertEquals(requestDTO.getContent(), review.getContent());
                        assertEquals(Double.parseDouble(requestDTO.getRate()), review.getRate());
                        assertEquals(requestDTO.getPros(), review.getPros());
                        assertEquals(requestDTO.getCons(), review.getCons());
                    });
        }

        @Test
        void whenWriteReviewWithInvalidHotelId_thenReturn404() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            WriteReviewRequestDTO requestDTO = WriteReviewRequestDTO.builder()
                    .hotelId(String.valueOf(-1L))
                    .content("Great hotel!")
                    .rate("8.6")
                    .pros("The staff was very friendly")
                    .cons("The room was a bit small")
                    .build();

            webTestClient.post()
                    .uri(EndPoints.Hotels.WRITE_HOTEL_REVIEW)
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class RemoveReview {
        @Test
        void whenRemoveReviewWithValidId_thenReturn200() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Hotels.REMOVE_HOTEL_REVIEW + "/1")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        void whenRemoveReviewWithInvalidId_thenReturn404() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Hotels.REMOVE_HOTEL_REVIEW + "/-1")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void whenRemoveReviewOfAnotherUser_thenReturn403() {
            AccessTokenResponseDTO accessTokenResponseDTO = SharedTestUtil.signInJohnDoeAndGetAccessToken(webTestClient);

            webTestClient.delete()
                    .uri(EndPoints.Hotels.REMOVE_HOTEL_REVIEW + "/3")
                    .header("Authorization", "Bearer " + accessTokenResponseDTO.getAccessToken())
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }

    @Nested
    class GetHotelReviews {
        @Test
        void whenGetHotelReviews_thenReturnHotelReviews() {
            webTestClient.get()
                    .uri(EndPoints.Hotels.GET_HOTEL_REVIEWS + "/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Review.class)
                    .hasSize(1);
        }
    }

    @Nested
    class GetPopularHotels {
        @Test
        void whenGetPopularHotels_thenReturnHotels() {
            webTestClient.get()
                    .uri(EndPoints.Hotels.GET_POPULAR_HOTELS)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(GetFilteredHotelsResponseDTO.class);
        }
    }
}