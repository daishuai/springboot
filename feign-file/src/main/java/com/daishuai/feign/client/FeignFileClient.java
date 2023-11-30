package com.daishuai.feign.client;

import com.alibaba.fastjson.JSONObject;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Tom
 * @version 1.0.0
 * @description TODO
 * @createTime 2023年08月30日 14:50:00
 */
@FeignClient(name = "feign-file", url = "http://localhost:8769/", configuration = FeignFileClient.FileUploadFeignConfiguration.class)
public interface FeignFileClient {

    @PostMapping(value = "/im/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    JSONObject uploadFile(@RequestPart("a") MultipartFile file, @RequestParam("moduleName") String moduleName,
                          @RequestParam("needChange") Boolean needChange, @RequestParam("index") Integer index,
                          @RequestParam("percent") Double percent);

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    JSONObject uploadFile(@RequestPart("file") MultipartFile file);

    class FileUploadFeignConfiguration {

        @Bean
        @Primary
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }

        @Bean
        public Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }
    }
}
