package com.acech.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Demo应用启动类
 *
 * @author wahcen@163.com
 * @date 2021/8/28 19:24
 */
@EnableAspectJAutoProxy
@EnableConfigurationProperties
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
