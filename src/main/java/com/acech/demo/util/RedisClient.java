package com.acech.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author wahcen@163.com
 * @date 2021/8/29 1:22
 */
@Getter
@Slf4j
@Component("redisClientUtil")
@RequiredArgsConstructor
@ConditionalOnBean(name = "strObjRedisTemplate")
public class RedisClient {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <K, V> RMap<K, V> getMap(String storeKey) {
        return new RMap<>(storeKey, redisTemplate);
    }

    public <T> RObject<T> getObject(String storeKey, Class<T> objClass) {
        return new RObject<>(redisTemplate, storeKey, objClass, objectMapper);
    }
}
