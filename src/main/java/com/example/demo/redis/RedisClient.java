package com.example.demo.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.config.RedisConfig;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {

    private final RedisConfig redisConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();



    public RedisClient(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    private Jedis getJedis(int db) {
        return redisConfig.getJedisPool(db).getResource();
    }

    public <T> T get(int db, String key, Class<T> clazz) {
        try (Jedis jedis = getJedis(db)) {
            String value = jedis.get(key);
            if (value == null) return null;
            return objectMapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void set(int db, String key, Object value) {
        try (Jedis jedis = getJedis(db)) {
            String json = objectMapper.writeValueAsString(value);
            jedis.set(key, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setWithTTL(int db, String key, Object value,
                           long timeout, TimeUnit unit) {
        try (Jedis jedis = getJedis(db)) {
            String json = objectMapper.writeValueAsString(value);
            jedis.setex(key, (int) unit.toSeconds(timeout), json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int db, String key) {
        try (Jedis jedis = getJedis(db)) {
            jedis.del(key);
        }
    }
}