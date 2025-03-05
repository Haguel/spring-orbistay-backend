package dev.haguel.orbistay.exception;

public class BankCardNotFoundException extends RuntimeException {
    public BankCardNotFoundException(String message) {
        super(message);
    }
}
