package dev.haguel.orbistay.serialization.app_user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.service.AppUserService;

import java.io.IOException;

public class ReviewAppUserDeserializer extends StdDeserializer<AppUser> {
    AppUserService appUserService;

    public ReviewAppUserDeserializer() {
        this(null);
    }

    public ReviewAppUserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AppUser deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Long id = node.get("id").asLong();
        String username = node.get("username").asText();
        String avatarUrl = node.get("avatarUrl").asText();

        AppUser appUser = new AppUser();
        appUser.setId(id);
        appUser.setUsername(username);
        appUser.setAvatarUrl(avatarUrl);

        return appUser;
    }
}
