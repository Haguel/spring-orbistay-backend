package dev.haguel.orbistay.exception;

public class HotelRoomNotFoundException extends RuntimeException {
    public HotelRoomNotFoundException(String message) {
        super(message);
    }
}
