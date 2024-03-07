package com.daishuai.docker.tomcat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Keda
 * @version 1.0.0
 * @description 测试接口
 * @createTime 2024年03月07日 18:46:00
 */
@RestController
public class DemoController {

    @GetMapping(value = "/hello")
    public ResponseEntity<Boolean> hello() {
        return ResponseEntity.ok(true);
    }
}
