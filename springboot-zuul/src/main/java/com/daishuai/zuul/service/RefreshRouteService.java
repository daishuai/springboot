package com.daishuai.zuul.service;

import com.daishuai.zuul.config.CustomRouteLocator;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author admin
 * @version 1.0.0
 * @description 刷新路由信息
 * @createTime 2022-08-08 22:57:12
 */
@Service
public class RefreshRouteService {

    @Resource
    private ApplicationEventPublisher publisher;

    @Resource
    private CustomRouteLocator customRouteLocator;

    public void refreshRoute() {
        customRouteLocator.setRouteConfig("{\"service-a\":{\"path\":\"/service-a/**\",\"url\":\"http://127.0.0.1:8090/service-a\"},\"service-b\":{\"path\":\"/service-b/**\",\"url\":\"http://127.0.0.1:8090/service-b\"},\"service-c\":{\"path\":\"/service-c/**\",\"url\":\"http://127.0.0.1:8090/service-c\"}}");
        RoutesRefreshedEvent event = new RoutesRefreshedEvent(customRouteLocator);
        publisher.publishEvent(event);
    }
}
