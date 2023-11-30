package com.daishuai.demo.job;

import com.alibaba.fastjson.JSON;
import com.daishuai.demo.config.DemoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Tom
 * @version 1.0.0
 * @description TODO
 * @createTime 2023年11月30日 14:45:00
 */
@Slf4j
@Component
public class DemoJob2 {
    @Resource
    private DemoProperties demoProperties;

    @Scheduled(cron = "${demo.job.corn:0/10 * * * * ?}")
    public void log() {
        log.info("Demo Properties: {}", JSON.toJSONString(demoProperties));
    }

}
