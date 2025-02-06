package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisService {
    private RedisTemplate<String, Object> template;

    public Object getFromCache(String key) {
        return template.opsForValue().get(key);
    }

    public void saveToCache(String key, Object object, long ttl, TimeUnit timeUnit) {
        template.opsForValue().set(key, object, ttl, timeUnit);
    }

    public void invalidateCache(String key) {
        template.delete(key);
    }
}