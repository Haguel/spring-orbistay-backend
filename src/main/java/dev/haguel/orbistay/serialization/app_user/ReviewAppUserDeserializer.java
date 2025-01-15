package dev.haguel.orbistay.serialization.app_user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.repository.AppUserRepository;
import dev.haguel.orbistay.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReviewAppUserDeserializer extends StdDeserializer<AppUser> {
    @Autowired
    private AppUserRepository appUserRepository;

    public ReviewAppUserDeserializer() {
        this(null);
    }

    public ReviewAppUserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AppUser deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Long id = node.get("id").asLong();

        return appUserRepository.findById(id).orElse(null);
    }
}
