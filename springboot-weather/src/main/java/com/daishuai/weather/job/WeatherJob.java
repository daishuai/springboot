package com.daishuai.weather.job;

import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.weather.common.CommonCache;
import com.daishuai.weather.common.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

/**
 * @author Daishuai
 * @description 爬天气数据Job
 * @date 2019/7/4 22:39
 */
@Slf4j
//@Component
public class WeatherJob {
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    @Scheduled(cron = "0 0/1 * * * ?")
    public void acquireWeatherData() {
        long start = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>>>>>>>>开始爬取天气数据<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String url = "http://d1.weather.com.cn/sk_2d/%s.html?_=%d";
        for (String code : CommonCache.cityMap.keySet()) {
            if (StringUtils.equalsAny(code, "101011900", "101081000", "101011800",
                    "101221601", "101012200", "101012100", "101012000", "101251301")) {
                continue;
            }
            String doc = HttpUtils.getGetResponse(null, url, code, System.currentTimeMillis());
            this.handle(doc);
        }
        log.info(">>>>>>>>>>>>>>>>>>>结束爬取天气数据，耗时：{}<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", System.currentTimeMillis() - start);
    }
    
    /**
     * 处理数据
     *
     * @param data
     */
    private void handle(String data) {
        String city = data.substring(data.indexOf("{"));
        restElasticsearchApi.upsertToProcessor("springboot_weather", "weather", UUID.randomUUID().toString().replace("-", ""), city);
    }
}
