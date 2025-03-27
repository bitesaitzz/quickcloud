package com.bitesaitzz.CloudService.services;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Map<String, Integer> limits;
    private final Map<String, Duration> timeWindows;


    @Autowired
    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.limits = new HashMap<>();
        this.timeWindows = new HashMap<>();

        limits.put("GET", 10);
        limits.put("POST", 5);
        timeWindows.put("GET", Duration.ofMinutes(1));
        timeWindows.put("POST", Duration.ofMinutes(1));
    }

    public boolean isAllowed(String userId, String type) {
        String key = "rate_limit::" + userId + "::" + type;
        Long requestCount = redisTemplate.opsForValue().increment(key); // Увеличиваем счетчик

        if (requestCount == 1) {
            redisTemplate.expire(key, timeWindows.get(type));
        }

        return requestCount <= limits.get(type); // Разрешаем, если не превышен лимит
    }




}
