package dev.haguel.orbistay.util.mapper;

import com.google.common.collect.Lists;
import dev.haguel.orbistay.dto.GetBrieflyHotelRoomsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.mapper.HotelRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelMapperUtil {
    private final HotelRoomMapper hotelRoomMapper;

    public List<String> getImagesUrls(Hotel hotel) {
        List<String> imagesUrls = Lists.newArrayList(hotel.getMainImageUrl());
        imagesUrls.addAll(hotel.getHotelRooms()
                .stream()
                .flatMap(hotelRoom -> hotelRoom.getImagesUrls().stream())
                .limit(19)
                .collect(Collectors.toList()));

        return imagesUrls;
    }

    public List<GetBrieflyHotelRoomsResponseDTO> getBrieflyHotelRoomsResponseDTOs(Hotel hotel) {
        List<GetBrieflyHotelRoomsResponseDTO> getBrieflyHotelRoomsResponseDTOs = hotel.getHotelRooms()
                .stream()
                .map(hotelRoomMapper::hotelRoomToBrieflyHotelRoomsDTO)
                .collect(Collectors.toList());

        return getBrieflyHotelRoomsResponseDTOs;
    }

    public double getReviewsAvgRate(Hotel hotel) {
        return hotel.getReviews().stream().mapToDouble(review -> review.getRate()).average().orElse(0);
    }
}
