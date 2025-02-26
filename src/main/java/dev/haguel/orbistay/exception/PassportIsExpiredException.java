package dev.haguel.orbistay.exception;

public class PassportIsExpiredException extends RuntimeException {
    public PassportIsExpiredException(String message) {
        super(message);
    }
}
