package com.daishuai.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @version 1.0.0
 * @description 自定义配置
 * @createTime 2023年11月30日 13:59:00
 */
@Data
@Component
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {

    private String url;

    private String username;

    private String password;
}
