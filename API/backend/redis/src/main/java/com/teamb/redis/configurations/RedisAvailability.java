package com.teamb.redis.configurations;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisAvailability {
    private final boolean redisAvailable;

    public RedisAvailability(RedisConnectionFactory redisConnectionFactory) {
        boolean isAvailable;
        try {
            redisConnectionFactory.getConnection().ping();
            isAvailable = true;
        } catch (Exception ex) {
            isAvailable = false;
        }
        this.redisAvailable = isAvailable;
    }

    public boolean isRedisAvailable() {
        return redisAvailable;
    }
}