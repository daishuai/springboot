package com.daishuai.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 启动类
 * @createTime 2024年03月04日 15:04:00
 */
@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @GetMapping(value = "/get/hello")
    public String get() {
        return "Hello World";
    }
}

