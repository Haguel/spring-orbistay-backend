package dev.haguel.orbistay.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.haguel.orbistay.entity.AppUser;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;

public class ReviewAppUserSerializer extends StdSerializer<AppUser> {
    public ReviewAppUserSerializer() {
        this(null);
    }

    public ReviewAppUserSerializer(Class<AppUser> t) {
        super(t);
    }

    @Override
    public void serialize(AppUser appUser, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        InnerSerializer innerSerializer = new InnerSerializer(appUser);
        jsonGenerator.writeStartObject();
        
        for (Field field : InnerSerializer.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                jsonGenerator.writeObjectField(field.getName(), field.get(innerSerializer));
            } catch (IllegalAccessException e) {
                throw new IOException("Error serializing field: " + field.getName(), e);
            }
        }

        jsonGenerator.writeEndObject();
    }

    @Getter
    public static class InnerSerializer {
        private Long id;
        private String username;
        private String avatarUrl;

        public InnerSerializer(AppUser appUser) {
            this.id = appUser.getId();
            this.username = appUser.getUsername();
            this.avatarUrl = appUser.getAvatarUrl();
        }
    }

}
