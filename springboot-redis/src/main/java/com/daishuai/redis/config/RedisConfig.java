package com.daishuai.redis.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.daishuai.redis.listener.Demo2RedisMessageListener;
import com.daishuai.redis.listener.DemoRedisMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.charset.StandardCharsets;

/**
 * @author Daishuai
 * @description Redis配置类
 * @date 2019/6/15 21:01
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.setTaskExecutor(springSessionRedisTaskExecutor());
        container.addMessageListener(demoRedisMessageListener(), new PatternTopic("/demo*"));
        container.addMessageListener(messageListenerAdapter(), new PatternTopic("__sentinel__:hello"));
        return container;
    }


    @Bean
    public ThreadPoolTaskExecutor springSessionRedisTaskExecutor(){
        ThreadPoolTaskExecutor springSessionRedisTaskExecutor = new ThreadPoolTaskExecutor();
        springSessionRedisTaskExecutor.setCorePoolSize(2);
        springSessionRedisTaskExecutor.setMaxPoolSize(5);
        springSessionRedisTaskExecutor.setKeepAliveSeconds(5);
        springSessionRedisTaskExecutor.setQueueCapacity(1000);
        springSessionRedisTaskExecutor.setThreadNamePrefix("spring-redis-");
        return springSessionRedisTaskExecutor;
    }
    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(demo2RedisMessageListener(), "handleMessage");
    }
    @Bean
    public Demo2RedisMessageListener demo2RedisMessageListener() {
        return new Demo2RedisMessageListener();
    }

    @Bean
    public MessageListener demoRedisMessageListener() {
        return new DemoRedisMessageListener();
    }
    @Bean
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer container) {
        return new KeyExpirationEventMessageListener(container);
    }
}
