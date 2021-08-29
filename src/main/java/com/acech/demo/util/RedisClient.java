package com.acech.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 一个封装的redis工具类
 *
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

    /**
     * 获得一个基于redis操作的Map集合
     * eg:
     *  Map<String, String> redisMap = redisClient.getMap("redisMap")
     *  redisMap.put("key", "value")
     *  redisMap.get("key")
     * @param storeKey 集合名称，redis实际存储的key
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @return Map集合
     */
    public <K, V> RMap<K, V> getMap(String storeKey) {
        return new RMap<>(storeKey, redisTemplate);
    }

    /**
     * 获得一个基于redis操作的Object包装类
     * eg:
     *  RObject<Demo> demo = redisClient.getObject("demo", Demo.class);
     *  demo.set(new Demo(1, "demo-class"))
     *  demo.ifPresent(d -> System.out.println(d.getName()));
     *  Demo d = demo.orElse(new Demo(2, "replace if not exist"));
     * @param storeKey 对象名称，redis实际存储的key
     * @param objClass 对象的类型
     * @param <T> 对象类型
     * @return 对象包装类
     * @throws IllegalArgumentException 不支持Number类及其子类的保存，默认的json序列化器在反序列化数字时异常
     */
    public <T> RObject<T> getObject(String storeKey, Class<T> objClass) {
        return new RObject<>(redisTemplate, storeKey, objClass, objectMapper);
    }

    /**
     * 获得一个基于redis操作的List集合
     * eg:
     *  RList<Demo> demoList = redisClient.getList("demoList", Demo.class);
     *  demoList.add(new Demo(1, "demo-item"));
     *  demoList.remove(0);
     *  demoList.forEach(item -> System.out.println(item.getName()));
     *  Demo d = demoList.get(0);
     * @param storeKey list名称，redis实际存储的key
     * @param objClass list元素的类型
     * @param <T> 元素类型
     * @return List集合
     */
    public <T> RList<T> getList(String storeKey, Class<T> objClass) {
        return new RList<>(redisTemplate, storeKey, objClass, objectMapper);
    }
}
