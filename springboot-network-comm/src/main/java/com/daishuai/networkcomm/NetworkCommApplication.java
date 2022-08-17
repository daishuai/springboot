package com.daishuai.networkcomm;

import com.daishuai.networkcomm.config.NetworkCommonProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 启动类
 * @createTime 2022年08月17日 16:17:00
 */
@SpringBootApplication
@EnableConfigurationProperties(value = {NetworkCommonProperties.class})
public class NetworkCommApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkCommApplication.class, args);
    }
}
