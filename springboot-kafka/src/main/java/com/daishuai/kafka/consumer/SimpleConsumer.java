package com.daishuai.kafka.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Daishuai
 * @description 简单消费者
 * @date 2019/6/15 18:54
 */
@Slf4j
@Component
public class SimpleConsumer {

    @KafkaListener(topics = "topic1")
    public void consumer(ConsumerRecord record) {
        Optional<Object> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {
            Object message = value.get();
            log.info("监听到一条消息，message:{}", JSON.toJSONString(message));
        }
    }
}
