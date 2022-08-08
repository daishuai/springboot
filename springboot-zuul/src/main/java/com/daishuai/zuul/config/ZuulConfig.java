package com.daishuai.zuul.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author admin
 * @version 1.0.0
 * @description Zuul配置类
 * @createTime 2022-08-08 22:51:12
 */
@Configuration
public class ZuulConfig {

    @Resource
    private ZuulProperties zuulProperties;

    @Resource
    private ServerProperties serverProperties;

    @Bean
    public RouteLocator customRouteLocator() {
        return new CustomRouteLocator(this.serverProperties.getServletPath(), zuulProperties);
    }
}
