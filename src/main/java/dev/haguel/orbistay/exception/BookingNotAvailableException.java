package dev.haguel.orbistay.exception;

public class BookingNotAvailableException extends RuntimeException {
    public BookingNotAvailableException(String message) {
        super(message);
    }
}
