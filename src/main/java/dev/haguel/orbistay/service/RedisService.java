package dev.haguel.orbistay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String AUTH_PREFIX = "auth:";
    private static final String RESET_PASSWORD_PREFIX = "reset_password:";

    public void setAuthValue(String key, String value) {
        redisTemplate.opsForValue().set(AUTH_PREFIX + key, value);
    }

    public String getAuthValue(String key) {
        return redisTemplate.opsForValue().get(AUTH_PREFIX + key);
    }

    public void deleteAuthValue(String key) {
        redisTemplate.delete(AUTH_PREFIX + key);
    }

    public boolean hasAuthKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(AUTH_PREFIX + key));
    }

    public void setResetPasswordValue(String key, String value) {
        redisTemplate.opsForValue().set(RESET_PASSWORD_PREFIX + key, value);
    }

    public String getResetPasswordValue(String key) {
        return redisTemplate.opsForValue().get(RESET_PASSWORD_PREFIX + key);
    }

    public void deleteResetPasswordValue(String key) {
        redisTemplate.delete(RESET_PASSWORD_PREFIX + key);
    }

    public boolean hasResetPasswordKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(RESET_PASSWORD_PREFIX + key));
    }
}
