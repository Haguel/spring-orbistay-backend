package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.entity.RecentlyViewedHotel;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.mapper.HotelMapper;
import dev.haguel.orbistay.repository.RecentlyViewedHotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecentlyViewedHotelService {
    private final HotelService hotelService;
    private final RecentlyViewedHotelRepository recentlyViewedHotelRepository;
    private final HotelMapper hotelMapper;

    @Transactional
    protected void delete(RecentlyViewedHotel recentlyViewedHotel) {
        recentlyViewedHotelRepository.delete(recentlyViewedHotel);

        log.info("Recently viewed hotel with id {} deleted", recentlyViewedHotel.getId());
    }

    @Transactional
    public void addToRecentlyViewedHotels(AppUser appUser, Long hotelId) throws HotelNotFoundException {
        RecentlyViewedHotel recentlyViewedHotel = RecentlyViewedHotel.builder()
                .appUser(appUser)
                .hotel(hotelService.findById(hotelId))
                .build();

        List<RecentlyViewedHotel> recentlyViewedHotels = appUser.getRecentlyViewedHotels();
        // Remove the rvHotel if it is already in the list
        recentlyViewedHotels.stream()
                .filter(rvHotel -> rvHotel.getHotel().getId().equals(hotelId))
                .findFirst()
                .ifPresent(this::delete);

        if(recentlyViewedHotels.size() >= 10) {
            while (recentlyViewedHotels.size() > 10) {
                delete(recentlyViewedHotels.get(0));
            }
        }

        recentlyViewedHotelRepository.save(recentlyViewedHotel);
        log.info("Hotel added to recently viewed hotels");
    }

    public List<GetHotelsResponseDTO> getRecentlyViewedHotels(AppUser appUser) {
        List<GetHotelsResponseDTO> recentlyViewedHotels = appUser.getMappedRecentlyViewedHotels()
                .stream().map(hotelMapper::hotelToHotelsResponseDTO).toList();

        log.info("Found {} recently viewed hotels", recentlyViewedHotels.size());
        return recentlyViewedHotels;
    }
}
