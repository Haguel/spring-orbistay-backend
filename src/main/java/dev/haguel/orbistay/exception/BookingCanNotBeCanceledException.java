package dev.haguel.orbistay.exception;

public class BookingCanNotBeCanceledException extends RuntimeException {
    public BookingCanNotBeCanceledException(String message) {
        super(message);
    }
}
