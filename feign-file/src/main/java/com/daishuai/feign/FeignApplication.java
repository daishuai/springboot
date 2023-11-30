package com.daishuai.feign;

import com.alibaba.fastjson.JSONObject;
import com.daishuai.feign.client.FeignFileClient;
import com.daishuai.feign.dto.MockMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;

/**
 * @author Tom
 * @version 1.0.0
 * @description TODO
 * @createTime 2023年08月30日 14:50:00
 */
@Slf4j
@EnableFeignClients
@RestController
@SpringBootApplication
public class FeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class, args);
    }

    @Resource
    private FeignFileClient feignFileClient;

    @GetMapping(value = "/upload")
    public Object upload() throws IOException {
        URL url = new URL("");
        byte[] body = IOUtils.toByteArray(url);
        // MultipartFile, name必须和上传文件接口参数名称保持一致
        MultipartFile file = new MockMultipartFile("file", "a.jpg", ContentType.IMAGE_JPEG.getMimeType(), body);
        JSONObject jsonObject = feignFileClient.uploadFile(file, "battleGroup", true, 6, 0.3);
        log.info("response: {}", jsonObject);
        return jsonObject;
    }
}
