package com.daishuai.redis.controller;

import com.daishuai.common.entity.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Daishuai
 * @description 测试用例
 * @date 2019/6/13 17:53
 */
@RequestMapping("/demo")
@RestController
public class DemoController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/redis")
    public ResponseEntity redis() {
        for (int i=0; i<5; i++) {
            stringRedisTemplate.opsForValue().set("key-" + i, UUID.randomUUID().toString().replace("-", ""));
        }
        return ResponseEntity.success();
    }
}
