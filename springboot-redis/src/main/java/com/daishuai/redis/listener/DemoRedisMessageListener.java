package com.daishuai.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author Daishuai
 * @version 1.0.0
 * @ClassName DemoRedisMessageListener.java
 * @Description TODO
 * @createTime 2022年05月29日 20:18:00
 */
@Slf4j
public class DemoRedisMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("body: {}, channel: {}, pattern: {}", new String(message.getBody()), new String(message.getChannel()), new String(pattern));
    }
}
