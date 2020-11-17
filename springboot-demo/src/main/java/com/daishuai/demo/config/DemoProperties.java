package com.daishuai.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Daishuai
 * @date 2020/8/6 10:40
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "demo.property")
public class DemoProperties {

    private Map<String, Map<String, String>> indexMap;

    private Integer interval;
}
