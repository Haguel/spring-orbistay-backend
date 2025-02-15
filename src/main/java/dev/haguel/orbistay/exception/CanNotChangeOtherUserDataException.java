package dev.haguel.orbistay.exception;

public class CanNotChangeOtherUserDataException extends RuntimeException {
    public CanNotChangeOtherUserDataException(String message) {
        super(message);
    }
}
