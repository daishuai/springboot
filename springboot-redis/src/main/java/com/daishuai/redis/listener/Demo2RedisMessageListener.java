package com.daishuai.redis.listener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Daishuai
 * @version 1.0.0
 * @ClassName Demo2RedisMessageListener.java
 * @Description TODO
 * @createTime 2022年05月29日 20:22:00
 */
@Slf4j
public class Demo2RedisMessageListener {

    public void handleMessage(String message) {
        log.info("message: {}", message);
    }
}
