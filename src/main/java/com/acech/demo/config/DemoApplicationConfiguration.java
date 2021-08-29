package com.acech.demo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * demo应用的自定义配置类
 *
 * @author wahcen@163.com
 * @date 2021/8/28 19:58
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "app.demo")
public class DemoApplicationConfiguration {
    private String appName;
    private String appSecret;
    private String appSalt;

    @PostConstruct
    public void doAfterConstruct() {
        log.info("Initializing app {}", appName);
        log.info("App secret {}", appSecret);
        log.info("App salt {}", appSalt);
    }
}
