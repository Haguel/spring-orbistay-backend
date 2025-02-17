package dev.haguel.orbistay.exception;

public class EmailVerificationNotFoundException extends RuntimeException {
    public EmailVerificationNotFoundException(String message) {
        super(message);
    }
}
