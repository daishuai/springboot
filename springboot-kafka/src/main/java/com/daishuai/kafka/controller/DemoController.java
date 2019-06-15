package com.daishuai.kafka.controller;

import com.daishuai.kafka.producer.SimpleProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daishuai
 * @description 测试用例
 * @date 2019/6/15 19:05
 */
@RequestMapping("/kafka")
@RestController
public class DemoController {

    @Autowired
    private SimpleProducer simpleProducer;

    @GetMapping("/send")
    public void sendMessage() {

        simpleProducer.send("topic1", "Hello Kafka, Welcome To Our World!");
    }
}
