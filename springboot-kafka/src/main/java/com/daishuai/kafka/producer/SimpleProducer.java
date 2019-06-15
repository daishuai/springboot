package com.daishuai.kafka.producer;

import com.alibaba.fastjson.JSON;
import com.daishuai.kafka.entity.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * @author Daishuai
 * @description 简单的生产者
 * @date 2019/6/15 18:57
 */
@Slf4j
@Component
public class SimpleProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        log.info("向topic：{} 发送一条消息：{}", topic, message);
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setUuid(key);
        messageEntity.setMessage(message);
        messageEntity.setSendTime(new Date());
        kafkaTemplate.send(topic, JSON.toJSONString(messageEntity));
    }
}
