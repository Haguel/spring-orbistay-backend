package dev.haguel.orbistay.exception;

public class BookingCanNotBeCanceled extends RuntimeException {
    public BookingCanNotBeCanceled(String message) {
        super(message);
    }
}
