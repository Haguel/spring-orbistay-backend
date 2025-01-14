package dev.haguel.orbistay.serialization.hotel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.haguel.orbistay.entity.Hotel;
import dev.haguel.orbistay.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReviewHotelDeserializer extends JsonDeserializer<Hotel> {
    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public Hotel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Long id = node.get("id").asLong();

        return hotelRepository.findById(id).orElse(null);
    }
}