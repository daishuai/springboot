package com.daishuai.redis.controller;

import com.daishuai.common.entity.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/redis/set")
    public ResponseEntity setRedis() {
        for (int i = 0; i < 5; i++) {
            stringRedisTemplate.opsForValue().set("key-" + i, UUID.randomUUID().toString().replace("-", ""));
        }
        return ResponseEntity.success();
    }
    
    
    @GetMapping("/redis/get")
    public ResponseEntity getRedis() {
        return ResponseEntity.success(stringRedisTemplate.opsForValue().get("key-1"));
    }
    @Scheduled(cron = "0/2 * * * * ?")
    public void task() {
        Map<String, Object> message = new HashMap<>();
        message.put("username", "123");
        redisTemplate.convertAndSend("/demoTopic", message);
    }
    //@Scheduled(fixedDelay = 40000)
    public void onceTask() {
        for (int i=0; i < 500; i++) {
            redisTemplate.opsForValue().set("expireKey" + i, "expireKey" + i, 10, TimeUnit.SECONDS);
        }
    }
}
