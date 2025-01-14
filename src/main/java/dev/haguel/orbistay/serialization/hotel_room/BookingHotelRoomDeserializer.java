package dev.haguel.orbistay.serialization.hotel_room;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.haguel.orbistay.entity.HotelRoom;
import dev.haguel.orbistay.repository.HotelRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BookingHotelRoomDeserializer extends JsonDeserializer<HotelRoom> {
    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Override
    public HotelRoom deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        Long hotelRoomId = node.get("id").asLong();
        return hotelRoomRepository.findById(hotelRoomId).orElse(null);
    }
}
