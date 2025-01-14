package dev.haguel.orbistay.serialization.hotel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.haguel.orbistay.entity.Hotel;

import java.io.IOException;

public class ReviewHotelDeserializer extends JsonDeserializer<Hotel> {
    @Override
    public Hotel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Long id = node.get("id").asLong();
        String name = node.get("name").asText();

        return Hotel.builder()
                .id(id)
                .name(name)
                .build();
    }
}