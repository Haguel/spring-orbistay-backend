package dev.haguel.orbistay.serialization.hotel_room;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.haguel.orbistay.entity.HotelRoom;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;

public class BookingHotelRoomSerializer extends JsonSerializer<HotelRoom> {
    @Override
    public void serialize(HotelRoom hotelRoom, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        InnerSerializer innerSerializer = new InnerSerializer(hotelRoom);
        jsonGenerator.writeStartObject();

        for (Field field : InnerSerializer.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(innerSerializer) == null) {
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

        public InnerSerializer(HotelRoom hotelRoom) {
            this.id = hotelRoom.getId();
        }
    }
}
