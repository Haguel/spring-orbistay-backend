package dev.haguel.orbistay.exception;

public class FavoritesNotFoundException extends RuntimeException {
    public FavoritesNotFoundException(String message) {
        super(message);
    }
}
