package dev.haguel.orbistay.exception;

public class EmailVerificationExpiredException extends RuntimeException {
    public EmailVerificationExpiredException(String message) {
        super(message);
    }
}
