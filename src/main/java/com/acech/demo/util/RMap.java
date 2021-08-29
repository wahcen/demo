package com.acech.demo.util;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 一个基于Redis的Map集合
 *
 * @author wahcen@163.com
 * @date 2021/8/29 1:35
 */
public class RMap<K, V> implements Map<K, V> {
    private final String storeKey;
    private final RedisTemplate<String, Object> redisTemplate;

    public RMap(String storeKey, RedisTemplate<String, Object> redisTemplate) {
        this.storeKey = storeKey;
        this.redisTemplate = redisTemplate;
    }

    private HashOperations<String, K, V> ops() {
        return redisTemplate.opsForHash();
    }

    @Override
    public int size() {
        return Math.toIntExact(ops().size(storeKey));
    }

    @Override
    public boolean isEmpty() {
        return ops().size(storeKey) == 0L;
    }

    @Override
    public boolean containsKey(Object key) {
        return ops().hasKey(storeKey, key);
    }

    @Override
    public boolean containsValue(Object value) {
        return ops().values(storeKey).contains(value);
    }

    @Override
    public V get(Object key) {
        return ops().get(storeKey, key);
    }

    @Override
    public V put(K key, V value) {
        ops().put(storeKey, key, value);
        return value;
    }

    @Override
    public V remove(Object key) {
        V v = ops().get(storeKey, key);
        ops().delete(storeKey, key);
        return v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        ops().putAll(storeKey, m);
    }

    @Override
    public void clear() {
        redisTemplate.delete(storeKey);
    }

    @Override
    public Set<K> keySet() {
        return ops().keys(storeKey);
    }

    @Override
    public Collection<V> values() {
        return ops().values(storeKey);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return ops().entries(storeKey).entrySet();
    }
}
