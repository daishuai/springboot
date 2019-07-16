package com.daishuai.weather.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Daishuai
 * @description Http请求工具
 * @date 2019/7/11 21:44
 */
@Slf4j
public class HttpUtils {
    
    private HttpUtils() {}
    
    public static String getGetResponse(Map<String, String> headers, String pattern, Object ... args) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        //创建并设置URI
        URIBuilder uri = null;
        //创建GET请求
        HttpGet httpGet = null;
        String url = String.format(pattern, args);
        try {
            uri = new URIBuilder(url);
            httpGet = new HttpGet(uri.build());
        } catch (URISyntaxException e) {
            log.info("创建并设置URI出错:{},url:{}", e.getMessage(), url);
        }
        if (httpGet == null) {
            return null;
        }
        if (MapUtils.isNotEmpty(headers)) {
        
        }
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        //创建响应对象
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            log.error("请求出错：{}", e.getMessage());
        }
        if (httpResponse == null) {
            return null;
        }
        //获取请求结果
        HttpEntity entity = httpResponse.getEntity();
        StringBuilder temp = new StringBuilder();
        try(
                InputStream inputStream = entity.getContent();
                Scanner scanner = new Scanner(inputStream, "UTF-8")
        ) {
            while (scanner.hasNextLine()) {
                temp.append(scanner.nextLine());
            }
        } catch (IOException e) {
            log.error("获取请求结果出错：{}", e.getMessage());
        }
        try {
            httpClient.close();
            httpResponse.close();
        } catch (IOException e) {
            log.error("关闭连接出错：{}", e.getMessage());
        }
        return temp.toString();
    }
}
