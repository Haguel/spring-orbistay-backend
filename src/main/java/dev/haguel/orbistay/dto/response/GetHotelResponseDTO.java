package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.entity.BookingCancelRule;
import dev.haguel.orbistay.entity.BookingPaymentOption;
import dev.haguel.orbistay.entity.HotelHighlight;
import dev.haguel.orbistay.entity.Review;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GetHotelResponseDTO", description = "Get hotel response data transfer object")
public class GetHotelResponseDTO {
    @Schema(description = "The hotel's ID", example = "1")
    private Long id;

    @Schema(description = "The hotel's name", example = "Royal Respite")
    private String name;

    @Schema(description = "The hotel's short description", example = "A luxurious hotel in the heart of New York")
    private String shortDesc;

    @Schema(description = "The hotel's full description", example = "A luxurious hotel in the heart of New York, offering the best services and amenities")
    private String fullDesc;

    @Schema(description = "The hotel's address", example = "New York, USA")
    private String address;

    @Schema(description = "The hotel's star rating", example = "5")
    private Integer stars;

    @Schema(description = "The hotel's images URLs", example = "[\"https://example.com/hotel.jpg\", \"https://example.com/hotel2.jpg\"]")
    private List<String> imagesUrls;

    @ArraySchema(schema = @Schema(implementation = HotelHighlight.class))
    @Schema(description = "The hotel's highlights")
    private List<HotelHighlight> hotelHighlights;

    @Schema(description = "The hotel's reviews count", example = "10")
    private int reviewsCount;

    @Schema(description = "The hotel's average rate", example = "4.5")
    private double avgRate;

    @Schema(description = "The hotel's check-in time", example = "14:00")
    private LocalTime checkInTime;

    @Schema(description = "The hotel's check-out time", example = "12:00")
    private LocalTime checkOutTime;

    @ArraySchema(schema = @Schema(implementation = Review.class))
    @Schema(description = "The hotel's reviews")
    private List<Review> reviews;

    @ArraySchema(schema = @Schema(implementation = GetBrieflyHotelRoomsResponseDTO.class))
    @Schema(description = "The hotel's rooms briefly data")
    private List<GetBrieflyHotelRoomsResponseDTO> hotelRooms;

    @ArraySchema(schema = @Schema(implementation = BookingPaymentOption.class))
    @Schema(description = "The hotel's booking payment options")
    private List<BookingPaymentOption> bookingPaymentOptions;

    @Schema(description = "The hotel's booking cancel rule")
    private BookingCancelRule bookingCancelRule;
}
