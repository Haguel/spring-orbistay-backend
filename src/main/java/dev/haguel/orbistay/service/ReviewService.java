package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.mapper.ReviewMapper;
import dev.haguel.orbistay.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final HotelService hotelService;
    private final ReviewMapper reviewMapper;

    public Review save(Review review) {
        review = reviewRepository.save(review);

        log.info("Review saved to database");
        return review;
    }

    public Review save(AppUser appUser, WriteReviewRequestDTO writeReviewRequestDTO) throws HotelNotFoundException {
        Hotel hotel = hotelService.findById(Long.valueOf(writeReviewRequestDTO.getHotelId()));
        Review review = reviewMapper.writeReviewRequestToReview(writeReviewRequestDTO);

        review.setHotel(hotel);
        review.setAppUser(appUser);
        return save(review);
    }
}
