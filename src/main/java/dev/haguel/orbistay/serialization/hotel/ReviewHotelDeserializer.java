package dev.haguel.orbistay.serialization.hotel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.haguel.orbistay.entity.Address;
import dev.haguel.orbistay.entity.Hotel;

import java.io.IOException;

public class ReviewHotelDeserializer extends JsonDeserializer<Hotel> {
    @Override
    public Hotel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Long id = node.get("id").asLong();
        String name = node.get("name").asText();
        String shortDesc = node.get("shortDesc").asText();
        String fullDesc = node.get("fullDesc").asText();
        Integer stars = (node.get("stars") != null) ? node.get("stars").asInt() : null;
        String mainImageUrl = node.get("mainImageUrl").asText();
        Address address = jsonParser.getCodec().treeToValue(node.get("address"), Address.class);

        return Hotel.builder()
                .id(id)
                .name(name)
                .shortDesc(shortDesc)
                .fullDesc(fullDesc)
                .stars(stars)
                .mainImageUrl(mainImageUrl)
                .address(address)
                .build();
    }
}