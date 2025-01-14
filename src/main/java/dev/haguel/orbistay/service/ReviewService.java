package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.WriteReviewRequestDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.entity.Review;
import dev.haguel.orbistay.exception.CanNotChangeOtherUserDataException;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.exception.ReviewNotFoundException;
import dev.haguel.orbistay.mapper.ReviewMapper;
import dev.haguel.orbistay.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final HotelService hotelService;
    private final ReviewMapper reviewMapper;

    private Review save(Review review) {
        review = reviewRepository.save(review);

        log.info("Review saved to database");
        return review;
    }

    private void delete(Review review) {
        reviewRepository.delete(review);
        log.info("Review with id {} deleted from database", review.getId());
    }

    @Transactional(readOnly = true)
    public Review findById(Long id) throws ReviewNotFoundException {
        Review review =  reviewRepository.findById(id).orElse(null);

        if (review == null) {
            log.warn("Review with id {} not found", id);
            throw new ReviewNotFoundException("Review with id " + id + " not found");
        }

        return review;
    }

    public Review save(AppUser appUser, WriteReviewRequestDTO writeReviewRequestDTO) throws HotelNotFoundException {
        Hotel hotel = hotelService.findById(Long.valueOf(writeReviewRequestDTO.getHotelId()));
        Review review = reviewMapper.writeReviewRequestToReview(writeReviewRequestDTO);

        review.setHotel(hotel);
        review.setAppUser(appUser);
        return save(review);
    }

    public void delete(AppUser appUser, Review review) throws CanNotChangeOtherUserDataException {
        if (review.getAppUser().getId() != appUser.getId()) {
            log.warn("User with id {} tried to delete review with id {} that does not belong to him", appUser.getId(), review.getId());
            throw new CanNotChangeOtherUserDataException("Can not delete review that does not belong to request user");
        }

        delete(review);
    }
}
