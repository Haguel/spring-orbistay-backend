package dev.haguel.orbistay.exception;

public class HotelRoomsNotFoundException extends RuntimeException {
    public HotelRoomsNotFoundException(String message) {
        super(message);
    }
}
