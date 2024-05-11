package com.daishuai.docker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Daishuai
 * @date 2020/6/18 23:05
 */
@Slf4j
@RestController
@RequestMapping(value = "/health")
public class HealthController {

    @Resource
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<String> success() {

        ResponseEntity<String> success = ResponseEntity.ok("200");
        try {
            log.info("health check: {}", objectMapper.writeValueAsString(success));
        } catch (JsonProcessingException e) {
            log.error("JSON 处理异常：{}", e.getMessage(), e);
        }
        return success;
    }
}
