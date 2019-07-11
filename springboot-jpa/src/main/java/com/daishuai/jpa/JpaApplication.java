package com.daishuai.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Daishuai
 * @description JPA
 * @date 2019/6/26 9:59
 */
@EnableScheduling
@SpringBootApplication
public class JpaApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }
}
