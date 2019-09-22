package com.daishuai.event.handler;

import com.daishuai.event.event.DemoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/2 17:07
 */
@Slf4j
@Order(10)
@Component
public class Demo2EventListener implements ApplicationListener<PayloadApplicationEvent<DemoEvent>> {
    
    @Override
    public void onApplicationEvent(PayloadApplicationEvent<DemoEvent> event) {
        DemoEvent payload = event.getPayload();
        log.info("Demo 2 >>>>>> payload:{}", payload);
    }
}
