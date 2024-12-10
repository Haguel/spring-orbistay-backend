package dev.haguel.orbistay.exception;

public class InvalidJwtTokenException extends Exception {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
