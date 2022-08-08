package com.daishuai.zuul.controller;

import com.daishuai.zuul.service.RefreshRouteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author admin
 * @version 1.0.0
 * @description 刷新路由接口
 * @createTime 2022-08-08 23:00:51
 */
@RestController
public class RefreshController {

    @Resource
    private RefreshRouteService refreshRouteService;

    @GetMapping("/refreshRoute")
    public String refreshRoute() {
        refreshRouteService.refreshRoute();
        return "Refresh Route Success";
    }
}
