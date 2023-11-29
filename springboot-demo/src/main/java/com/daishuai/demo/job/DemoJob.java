package com.daishuai.demo.job;

import com.daishuai.demo.mapper.JobConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Keda
 * @version 1.0.0
 * @description 动态任务
 * @createTime 2023年11月29日 15:08:00
 */
@Slf4j
@Configuration
public class DemoJob implements SchedulingConfigurer {

    @Resource
    private JobConfigMapper jobConfigMapper;
    public static String cron;
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        cron = jobConfigMapper.queryJobConfig("1");
        log.info("当前配置: {}", cron);
        Runnable task = () -> log.info("Current Time: {}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        Trigger trigger = (triggerContext) -> {
            cron = jobConfigMapper.queryJobConfig("1");
            log.info("当前配置: {}", cron);
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        };
        scheduledTaskRegistrar.addTriggerTask(task, trigger);
    }
}
