package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.timeout}")
    private int timeout;

    private final Map<Integer, JedisPool> poolMap = new ConcurrentHashMap<>();

    public JedisPool getJedisPool(int dbIndex) {
        return poolMap.computeIfAbsent(dbIndex, db -> {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(10);
            config.setMaxIdle(5);
            config.setMinIdle(1);

            return new JedisPool(
                    config,
                    host,
                    port,
                    timeout,
                    null,
                    db
            );
        });
    }
}