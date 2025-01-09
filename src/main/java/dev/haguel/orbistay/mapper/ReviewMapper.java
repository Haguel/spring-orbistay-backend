package dev.haguel.orbistay.mapper;

import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.util.mapper.SharedMapperUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Autowired
    protected SharedMapperUtil sharedMapperUtil;

    @Mapping(target = "rate", expression = "java(sharedMapperUtil.convertStringToDouble(writeReviewRequestDTO.getRate()))")
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "appUser", ignore = true)
    public abstract Review writeReviewRequestToReview(WriteReviewRequestDTO writeReviewRequestDTO);

    @AfterMapping
    protected void validateReviewRate(@MappingTarget Review review) {
        if (review.getRate() == null || review.getRate() < 1 || review.getRate() > 10) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Review rate is invalid");
        }
    }
}
