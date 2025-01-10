package dev.haguel.orbistay.dto.request.enumeration;

import lombok.Getter;

@Getter
public enum HotelStars {
    ONE_STAR("1"),
    TWO_STARS("2"),
    THREE_STARS("3"),
    FOUR_STARS("4"),
    FIVE_STARS("5");

    public final String stars;

    HotelStars(String stars) {
        this.stars = stars;
    }
}
