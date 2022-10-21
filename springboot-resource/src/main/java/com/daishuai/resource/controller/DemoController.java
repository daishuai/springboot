package com.daishuai.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 * @version 1.0.0
 * @description 测试接口
 * @createTime 2022-10-21 22:18:23
 */
@RestController
public class DemoController {

    @GetMapping(value = "/demo")
    public String demo() {
        return "200";
    }
}
