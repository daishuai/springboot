package com.daishuai.weather.job;

import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.weather.common.CommonCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author Daishuai
 * @description 爬天气数据Job
 * @date 2019/7/4 22:39
 */
@Slf4j
@Component
public class WeatherJob {
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    @Scheduled(cron = "0 0/1 * * * ?")
    public void acquireWeatherData() throws URISyntaxException, IOException {
        long start = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>>>>>>>>开始爬取天气数据<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .setStaleConnectionCheckEnabled(true)
                .build();
        
        
        String url = "http://d1.weather.com.cn/sk_2d/%s.html?_=%d";
        //String url = "http://m.weather.com.cn/data/%s.html?_=%d";
        for (String code : CommonCache.cityMap.keySet()) {
            if (StringUtils.equalsAny(code, "101011900", "101081000", "101011800",
                    "101221601", "101012200", "101012100", "101012000", "101251301")) {
                continue;
            }
            //创建HttpClient对象
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();
            //创建并设置URI
            URIBuilder uri = new URIBuilder(String.format(url, code, System.currentTimeMillis()));
            log.info("code:{}", code);
            //创建GET请求
            HttpGet httpGet = new HttpGet(uri.build());
            
            
            // 设置请求头
            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            httpGet.setHeader("Cache-Control", "no-cache");
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("Host", "d1.weather.com.cn");
            httpGet.setHeader("Upgrade-Insecure-Requests", "1");
            httpGet.setHeader("Cookie", "f_city=%E9%83%91%E5%B7%9E%7C101180101%7C; Hm_lvt_080dabacb001ad3dc8b9b9049b36d43b=1546482322; Hm_lpvt_080dabacb001a");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36");
            httpGet.setHeader("Referer", "http://www.weather.com.cn/weather1dn/101180101.shtml");
            
            //创建响应对象
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            
            //获取请求结果
            HttpEntity entity = httpResponse.getEntity();
            InputStream inputStream = entity.getContent();
            Scanner scanner = new Scanner(inputStream, "UTF-8");
            StringBuilder temp = new StringBuilder();
            while (scanner.hasNextLine()) {
                temp.append(scanner.nextLine());
            }
            
            log.info(temp.toString());
            this.handle(temp.toString());
            httpClient.close();
            httpResponse.close();
        }
        log.info(">>>>>>>>>>>>>>>>>>>结束爬取天气数据，耗时：{}<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", System.currentTimeMillis() - start);
    }
    
    /**
     * 处理数据
     * @param data
     */
    private void handle(String data) {
        String city = data.substring(data.indexOf("{"));
        restElasticsearchApi.upsertToProcessor("keda", "weather", UUID.randomUUID().toString().replace("-", ""), city);
    }
}
