package com.daishuai.weather.controller;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author Daishuai
 * @description 文件下载
 * @date 2019/7/5 20:13
 */
@RestController
@RequestMapping("/file")
public class FileDownloadController {
    
    @GetMapping("/download")
    public void download(String filePath, HttpServletResponse response) throws Exception {
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .setStaleConnectionCheckEnabled(true)
                .build();
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        //创建并设置URI
        URIBuilder uri = new URIBuilder(filePath);
        //创建GET请求
        HttpGet httpGet = new HttpGet(uri.build());
        
        //创建响应对象
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        
        //获取请求结果
        HttpEntity entity = httpResponse.getEntity();
        InputStream inputStream = entity.getContent();
        response.setHeader("content-type", "application/vnd.ms-excel");
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName));
        
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] buff = new byte[1024];
        int read = inputStream.read(buff);
        while (read != -1) {
            outputStream.write(buff, 0, buff.length);
            outputStream.flush();
            read = inputStream.read(buff);
        }
        outputStream.close();
        inputStream.close();
    }
}
