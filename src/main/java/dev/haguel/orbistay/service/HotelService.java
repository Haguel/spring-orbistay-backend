package dev.haguel.orbistay.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.haguel.orbistay.dto.request.GetFileredHotelRoomsRequestDTO;
import dev.haguel.orbistay.dto.request.GetFilteredHotelsRequestDTO;
import dev.haguel.orbistay.dto.request.HotelFiltersDTO;
import dev.haguel.orbistay.dto.request.enumeration.HotelStars;
import dev.haguel.orbistay.dto.request.enumeration.ObjectValuation;
import dev.haguel.orbistay.dto.response.FilteredHotelDTO;
import dev.haguel.orbistay.dto.response.GetFilteredHotelsResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelResponseDTO;
import dev.haguel.orbistay.dto.response.GetHotelsResponseDTO;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.HotelNotFoundException;
import dev.haguel.orbistay.exception.HotelRoomNotFoundException;
import dev.haguel.orbistay.exception.HotelsNotFoundException;
import dev.haguel.orbistay.mapper.HotelMapper;
import dev.haguel.orbistay.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final HotelRoomService hotelRoomService;

    private List<HashMap<? extends Enum, Integer>> getStatistics(List<Hotel> hotels) {
        Multimap<ObjectValuation, Hotel> hotelsByValuations = ArrayListMultimap.create();
        Multimap<HotelStars, Hotel> hotelsByStars = ArrayListMultimap.create();

        hotels.stream().forEach(hotel -> {
            hotelsByStars.put(HotelStars.fromStars(hotel.getStars()), hotel);
            hotelsByValuations.put(ObjectValuation.fromRate(hotel.getAvgRate()), hotel);
        });

        HashMap<HotelStars, Integer> hotelsCountByStars = hotelsByStars.keySet().stream()
                .collect(Collectors.toMap(
                        star -> star,
                        star -> hotelsByStars.get(star).size(),
                        (a, b) -> a,
                        HashMap::new
                ));

        HashMap<ObjectValuation, Integer> hotelsCountByValuations = hotelsByValuations.keySet().stream()
                .collect(Collectors.toMap(
                        valuation -> valuation,
                        valuation -> hotelsByValuations.get(valuation).size(),
                        (a, b) -> a,
                        HashMap::new
                ));

        return List.of(hotelsCountByStars, hotelsCountByValuations);
    }

    @Transactional(readOnly = true)
    public GetFilteredHotelsResponseDTO getFilteredHotels(GetFilteredHotelsRequestDTO getFilteredHotelsRequestDTO)
            throws HotelsNotFoundException {
        Integer peopleCount = Optional.ofNullable(getFilteredHotelsRequestDTO.getPeopleCount()).map(Integer::parseInt).orElse(null);
        Boolean isChildrenFriendly = Optional.ofNullable(getFilteredHotelsRequestDTO.getIsChildrenFriendly()).map(Boolean::parseBoolean).orElse(null);
        LocalDate checkIn = Optional.ofNullable(getFilteredHotelsRequestDTO.getCheckIn()).map(LocalDate::parse).orElse(null);
        LocalDate checkOut = Optional.ofNullable(getFilteredHotelsRequestDTO.getCheckOut()).map(LocalDate::parse).orElse(null);
        Double minPrice = Optional.ofNullable(getFilteredHotelsRequestDTO.getFilters())
                .map(HotelFiltersDTO::getMinPrice)
                .map(Double::parseDouble)
                .orElse(null);
        Double maxPrice = Optional.ofNullable(getFilteredHotelsRequestDTO.getFilters())
                .map(HotelFiltersDTO::getMaxPrice)
                .map(Double::parseDouble)
                .orElse(null);
        List<Integer> stars = Optional.ofNullable(getFilteredHotelsRequestDTO.getFilters())
                .map(HotelFiltersDTO::getStars)
                .map(starsList -> starsList.stream()
                        .map(star -> Integer.parseInt(star.stars))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        Boolean sevenToEight = Optional.ofNullable(getFilteredHotelsRequestDTO.getFilters())
                .map(HotelFiltersDTO::getValuations)
                .map(valuations -> valuations.contains(ObjectValuation.GOOD))
                .orElse(false);
        Boolean eightToNine = Optional.ofNullable(getFilteredHotelsRequestDTO.getFilters())
                .map(HotelFiltersDTO::getValuations)
                .map(valuations -> valuations.contains(ObjectValuation.VERY_GOOD))
                .orElse(false);
        Boolean nineToTen = Optional.ofNullable(getFilteredHotelsRequestDTO.getFilters())
                .map(HotelFiltersDTO::getValuations)
                .map(valuations -> valuations.contains(ObjectValuation.EXCELLENT))
                .orElse(false);

        List<Hotel> filteredHotels = hotelRepository.findFilteredHotels(
                getFilteredHotelsRequestDTO.getName(),
                getFilteredHotelsRequestDTO.getCity(),
                getFilteredHotelsRequestDTO.getCountry(),
                peopleCount,
                isChildrenFriendly,
                checkIn,
                checkOut,
                minPrice,
                maxPrice
            ).orElse(null);

        if (filteredHotels == null || filteredHotels.isEmpty()) {
            throw new HotelsNotFoundException("No hotels found for given criteria");
        }
        log.info("Found {} hotels", filteredHotels.size());

        // Get statistics by stars and valuations
        List<HashMap<? extends Enum, Integer>> statistics = getStatistics(filteredHotels);
        HashMap<HotelStars, Integer> hotelsCountByStars = (HashMap<HotelStars, Integer>) statistics.get(0);
        HashMap<ObjectValuation, Integer> hotelsCountByValuations = (HashMap<ObjectValuation, Integer>) statistics.get(1);

        // Additional filtering by stars and valuations
        filteredHotels = filteredHotels.stream()
                .filter(hotel -> stars.isEmpty() || stars.contains(hotel.getStars()))
                .filter((Hotel hotel) ->  {
                    double avgRate = hotel.getAvgRate();
                    return (!sevenToEight && !eightToNine && !nineToTen)
                            || (sevenToEight && avgRate >= 7 && avgRate < 8)
                            || (eightToNine && avgRate >= 8 && avgRate < 9)
                            || (nineToTen && avgRate >= 9 && avgRate <= 10);
                })
                .toList();
        List<FilteredHotelDTO> hotelsResponses = filteredHotels.stream()
                .map(hotelMapper::hotelToFilteredHotelDTO)
                .collect(Collectors.toList());


        // Set suitable hotel room for each hotel
        for (FilteredHotelDTO hotelResponse : hotelsResponses) {
            GetFileredHotelRoomsRequestDTO getFileredHotelRoomsRequestDTO = GetFileredHotelRoomsRequestDTO.builder()
                    .hotelId(hotelResponse.getId().toString())
                    .peopleCount(getFilteredHotelsRequestDTO.getPeopleCount())
                    .isChildrenFriendly(getFilteredHotelsRequestDTO.getIsChildrenFriendly())
                    .checkIn(getFilteredHotelsRequestDTO.getCheckIn())
                    .checkOut(getFilteredHotelsRequestDTO.getCheckOut())
                    .minPrice(getFilteredHotelsRequestDTO.getFilters().getMinPrice())
                    .maxPrice(getFilteredHotelsRequestDTO.getFilters().getMaxPrice())
                    .build();

            try {
                hotelResponse.setHotelRoom(hotelRoomService.getHotelRoom(getFileredHotelRoomsRequestDTO));
            } catch (HotelRoomNotFoundException e) {
                log.error("No hotel room found for the given criteria but hotel is found");
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        GetFilteredHotelsResponseDTO getFilteredHotelsResponseDTO = GetFilteredHotelsResponseDTO.builder()
                .hotels(hotelsResponses)
                .hotelsCountByStars(hotelsCountByStars
                        .entrySet().stream()
                        .map(entry -> new GetFilteredHotelsResponseDTO.HotelsCountByStars(entry.getKey(), entry.getValue()))
                        .toList()
                )
                .hotelsCountByValuations(hotelsCountByValuations
                        .entrySet().stream()
                        .map(entry -> new GetFilteredHotelsResponseDTO.HotelsCountByValuations(entry.getKey(), entry.getValue()))
                        .toList()
                )
                .build();

        log.info("Returning {} hotels", hotelsResponses.size());
        return getFilteredHotelsResponseDTO;
    }

    @Transactional(readOnly = true)
    public GetHotelResponseDTO getHotelById(Long id)
            throws HotelNotFoundException {
        Hotel hotel = findById(id);
        GetHotelResponseDTO getHotelResponseDTO = hotelMapper.hotelToHotelResponseDTO(hotel);

        return getHotelResponseDTO;
    }

    @Transactional(readOnly = true)
    public Hotel findById(Long id) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(id).orElse(null);

        if (hotel == null) {
            log.warn("Hotel with id {} not found", id);
            throw new HotelNotFoundException("Hotel not found");
        }

        return hotel;
    }

    @Transactional(readOnly = true)
    public List<GetHotelsResponseDTO> getPopularHotels() {
        List<Hotel> hotels = getPopularHotelsRaw();

        List<GetHotelsResponseDTO> getHotelsResponseDTOs = hotels.stream()
                .map(hotelMapper::hotelToHotelsResponseDTO)
                .collect(Collectors.toList());

        log.info("Returning {} popular hotels", getHotelsResponseDTOs.size());
        return getHotelsResponseDTOs;
    }

    @Transactional(readOnly = true)
    public List<Hotel> getPopularHotelsRaw() {
        List<Hotel> hotels = hotelRepository.findPopularHotels(LocalDate.now().minusDays(30));

        log.info("Found {} popular hotels", hotels.size());
        return hotels;
    }
}
