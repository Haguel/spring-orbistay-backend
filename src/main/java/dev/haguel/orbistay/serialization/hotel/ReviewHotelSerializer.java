package dev.haguel.orbistay.serialization.hotel;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.haguel.orbistay.entity.Address;
import dev.haguel.orbistay.entity.Hotel;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;

public class ReviewHotelSerializer extends JsonSerializer<Hotel> {
    @Override
    public void serialize(Hotel hotel, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        InnerSerializer innerSerializer = new InnerSerializer(hotel);
        jsonGenerator.writeStartObject();

        for (Field field : InnerSerializer.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if(field.get(innerSerializer) == null) {
                    jsonGenerator.writeNullField(field.getName());
                } else {
                    jsonGenerator.writeObjectField(field.getName(), field.get(innerSerializer));
                }
            } catch (IllegalAccessException e) {
                throw new IOException("Error serializing field: " + field.getName(), e);
            }
        }

        jsonGenerator.writeEndObject();
    }

    @Getter
    public static class InnerSerializer {
        private Long id;
        private String name;
        private String shortDesc;
        private String fullDesc;
        private Integer stars;
        private String mainImageUrl;
        private Address address;

        public InnerSerializer(Hotel hotel) {
            this.id = hotel.getId();
            this.name = hotel.getName();
            this.shortDesc = hotel.getShortDesc();
            this.fullDesc = hotel.getFullDesc();
            this.stars = hotel.getStars();
            this.mainImageUrl = hotel.getMainImageUrl();
            this.address = hotel.getAddress();
        }
    }
}