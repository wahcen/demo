package com.acech.demo.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @author wahcen@163.com
 * @date 2021/8/29 0:38
 */
@Slf4j
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisSerializer<Object> getCustomRedisSerializer() {
        return new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                if (o == null) {
                    return new byte[0];
                }
                return JSONUtil.toJsonStr(o).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                String encoded = new String(bytes, StandardCharsets.UTF_8);
                return JSONUtil.toBean(encoded, Object.class);
            }
        };
    }

    @Bean
    @Primary
    public RedisSerializer<Object> getJackson2JsonRedisSerializer() {
        return new Jackson2JsonRedisSerializer<>(Object.class);
    }

    @Bean
    public RedisSerializer<String> getStringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean("strObjRedisTemplate")
    public RedisTemplate<String, Object> getRedisTemplate(@Autowired RedisConnectionFactory connectionFactory,
                                                          @Autowired RedisSerializer<Object> jsonSerializer,
                                                          @Autowired RedisSerializer<String> stringSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        // hashKey和hashValue的序列化器使用默认的jdk序列化器
        // 因为默认的json序列化器，json序列化数字后反序列化会报错
        // 因为反序列化后的数字不是{}包裹的字符串
        return template;
    }
}
