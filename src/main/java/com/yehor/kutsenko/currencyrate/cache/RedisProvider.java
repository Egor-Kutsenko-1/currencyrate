package com.yehor.kutsenko.currencyrate.cache;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Component
@Setter
@RequiredArgsConstructor
public class RedisProvider {

    @Value("${spring.application.name}")
    private String applicationName;

    private final RedisTemplate<String, Object> redisTemplate;

    public <T> T getOrAdd(String key, Duration expiration, Supplier<T> retrieveFunction) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        T data = (T) ops.get(key);
        if (data != null) {
            return data;
        }

        data = retrieveFunction.get();
        ops.set(key, data, expiration);

        return data;
    }

    public String buildKey(String key) {
        return String.format("%s:%s", applicationName, key);
    }
}
