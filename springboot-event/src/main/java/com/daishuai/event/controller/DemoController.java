package com.daishuai.event.controller;

import com.daishuai.event.event.DemoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/10 16:49
 */
@Slf4j
@RestController
public class DemoController {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    
    @GetMapping("/publishEvent")
    public Object publishEvent() {
        DemoEvent event = new DemoEvent();
        event.setType("事件类型");
        event.setData("事件数据");
        log.info("发布事件DemoEvent");
        applicationContext.publishEvent(event);
        return event;
    }
}
