package dev.haguel.orbistay.exception;

public class IncorrectAuthDataException extends RuntimeException {
    public IncorrectAuthDataException(String message) {
        super(message);
    }
}
