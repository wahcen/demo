package com.acech.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 一个基于redis template的 String - Object 简易工具
 *
 * @author wahcen@163.com
 * @date 2021/8/29 1:58
 */
public class RObject<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String storeKey;
    private final Class<T> objClass;
    private final ObjectMapper objectMapper;

    public RObject(RedisTemplate<String, Object> redisTemplate, String storeKey, Class<T> objClass, ObjectMapper objectMapper) {
        if (Number.class.isAssignableFrom(objClass)) {
            throw new IllegalArgumentException("Please use RAtomicLong to replace use of RObject.");
        }
        this.redisTemplate = redisTemplate;
        this.storeKey = storeKey;
        this.objClass = objClass;
        this.objectMapper = objectMapper;
    }

    private ValueOperations<String, Object> ops() {
        return redisTemplate.opsForValue();
    }

    public T get() {
        Object o = ops().get(storeKey);
        if (o != null) {
            return objectMapper.convertValue(o, objClass);
        }
        return null;
    }

    public void set(T obj) {
        ops().set(storeKey, obj);
    }

    public void set(T obj, long timeout, TimeUnit timeUnit) {
        ops().set(storeKey, obj, timeout, timeUnit);
    }

    public void set(T obj, long timeout) {
        set(obj, timeout, TimeUnit.MILLISECONDS);
    }

    public T getAndSet(T newObj) {
        T oldObj = get();
        set(newObj);
        return oldObj;
    }

    public Boolean exists() {
        return redisTemplate.hasKey(storeKey);
    }

    public RObject<T> ifPresent(Consumer<T> consumer) {
        if (exists()) {
            consumer.accept(get());
        }
        return this;
    }

    public T orElse(T replace) {
        if (!exists()) {
            return replace;
        }
        return get();
    }

    public Boolean setIfAbsent(T obj) {
        return ops().setIfAbsent(storeKey, obj);
    }

    public T clear() {
        T obj = get();
        redisTemplate.delete(storeKey);
        return obj;
    }
}
