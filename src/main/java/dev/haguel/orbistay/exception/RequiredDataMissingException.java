package dev.haguel.orbistay.exception;

public class RequiredDataMissingException extends RuntimeException {
    public RequiredDataMissingException(String message) {
        super(message);
    }
}
