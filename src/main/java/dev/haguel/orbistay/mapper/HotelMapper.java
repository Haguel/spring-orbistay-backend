package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.util.mapper.HotelMapperUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class HotelMapper {
    @Autowired
    protected HotelMapperUtil hotelMapperUtil;

    @Mapping(target = "reviewsCount", expression = "java(hotel.getReviews().size())")
    @Mapping(target = "avgRate", expression = "java(hotelMapperUtil.getReviewsAvgRate(hotel))")
    public abstract GetHotelsResponseDTO hotelToHotelsResponseDTO(Hotel hotel);

    @Mapping(target = "address", expression = "java(hotel.getAddress().toString())")
    @Mapping(target = "reviewsCount", expression = "java(hotel.getReviews().size())")
    @Mapping(target = "avgRate", expression = "java(hotelMapperUtil.getReviewsAvgRate(hotel))")
    @Mapping(target = "imagesUrls", expression = "java(hotelMapperUtil.getImagesUrls(hotel))")
    @Mapping(target = "hotelRooms", expression = "java(hotelMapperUtil.getBrieflyHotelRoomsResponseDTOs(hotel))")
    public abstract GetHotelResponseDTO hotelToHotelResponseDTO(Hotel hotel);
}