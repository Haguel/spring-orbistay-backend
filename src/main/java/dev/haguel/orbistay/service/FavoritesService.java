package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.Favorites;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.exception.CanNotChangeOtherUserDataException;
import dev.haguel.orbistay.exception.FavoritesNotFoundException;
import dev.haguel.orbistay.repository.FavoriteHotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Slf4j
public class FavoritesService {
    private final FavoriteHotelRepository favoriteHotelRepository;

    private Favorites save(Favorites favorites) {
        favorites = favoriteHotelRepository.save(favorites);
        log.info("Favorites saved");

        return favorites;
    }

    private void delete(Favorites favorites) {
        favoriteHotelRepository.delete(favorites);
        log.info("Favorites deleted");
    }

    public void addHotelToFavorites(AppUser appUser, Hotel hotel) {
        Favorites existed = appUser.getFavorites()
                .stream()
                .filter(favorites -> favorites.getHotel().getId().equals(hotel.getId()))
                .findFirst()
                .orElse(null);

        if(existed != null) {
            log.info("Hotel already in favorites");
            return;
        }

        Favorites favorites = Favorites.builder()
                .appUser(appUser)
                .hotel(hotel)
                .build();

        save(favorites);
    }

    @Transactional(readOnly = true)
    public Favorites findById(Long id) throws FavoritesNotFoundException {
        Favorites favorites = favoriteHotelRepository.findById(id)
                .orElse(null);

        if(favorites == null) {
            log.info("Favorites with id {} not found", id);
            throw new FavoritesNotFoundException("Favorites not found");
        }

        return favorites;
    }

    public void removeHotelFromFavorites(AppUser appUser, Favorites favorites) throws CanNotChangeOtherUserDataException {
        if(favorites.getAppUser().getId().equals(appUser.getId())) {
            delete(favorites);
        } else {
            throw new CanNotChangeOtherUserDataException("Can't delete other user's favorites");
        }
    }
}
