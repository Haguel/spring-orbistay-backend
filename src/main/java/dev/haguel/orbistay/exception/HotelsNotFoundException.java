package dev.haguel.orbistay.exception;

public class HotelsNotFoundException extends RuntimeException {
    public HotelsNotFoundException(String message) {
        super(message);
    }
}
