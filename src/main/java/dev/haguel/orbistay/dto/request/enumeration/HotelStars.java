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

    public static HotelStars fromStars(int stars) {
        switch (stars) {
            case 1:
                return ONE_STAR;
            case 2:
                return TWO_STARS;
            case 3:
                return THREE_STARS;
            case 4:
                return FOUR_STARS;
            case 5:
                return FIVE_STARS;
            default:
                return null;
        }
    }
}
