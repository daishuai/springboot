package com.daishuai.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Daishuai
 * @description 爬天气数据启动类
 * @date 2019/7/4 22:37
 */
@EnableScheduling
@SpringBootApplication
public class WeatherApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
