package com.daishuai.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

/**
 * @author Daishuai
 * @version 1.0.0
 * @ClassName RedisKeyExpireListener.java
 * @Description TODO
 * @createTime 2022年05月29日 21:09:00
 */
@Slf4j
@Component
public class RedisKeyExpireListener implements ApplicationListener<RedisKeyExpiredEvent> {
    @Override
    public void onApplicationEvent(RedisKeyExpiredEvent event) {
        log.info("event: {}", event.getKeyspace());
    }
}
