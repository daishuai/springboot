package com.daishuai.networkcomm.controller;

import com.daishuai.networkcomm.model.TcpMessageModel;
import com.daishuai.networkcomm.service.MessageSendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description 测试类
 * @createTime 2022-08-17 23:01:33
 */
@RestController
public class DemoController {

    @Resource
    private MessageSendService messageSendService;

    @GetMapping(value = "/send")
    public TcpMessageModel send() {
        TcpMessageModel message = new TcpMessageModel();
        message.setType("messageBus");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("messageCode", 1000);
        dataMap.put("receiver", "list");
        dataMap.put("sender", "wangwu");
        message.setContent(dataMap);
        messageSendService.sendMessage(message);
        return message;
    }
}
