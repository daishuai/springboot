package com.daishuai.demo;

import com.daishuai.demo.config.DemoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/26 10:56
 */
@Slf4j
@RestController
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private DemoProperties demoProperties;

    @GetMapping("/demo")
    public Object demo() {
        //18697960017
        //log.info(JSON.toJSONString(demoProperties));
        return demoProperties.getIndexMap();
    }
}
