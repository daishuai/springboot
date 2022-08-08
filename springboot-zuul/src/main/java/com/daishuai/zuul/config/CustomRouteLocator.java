package com.daishuai.zuul.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description 自定义路由定位器
 * @createTime 2022-08-08 22:20:39
 */
@Slf4j
public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    private String routeConfig = "{\"service-a\":{\"path\":\"/service-as/**\",\"url\":\"http://127.0.0.1:8090/service-as\"},\"service-b\":{\"path\":\"/service-b/**\",\"url\":\"http://127.0.0.1:8090/service-b\"}}";

    public CustomRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    @Override
    protected void doRefresh() {
        super.doRefresh();
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {

        Map<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        // 从application.yml中加载路由信息
        routesMap.putAll(super.locateRoutes());
        // 从DB加载路由信息
        routesMap.putAll(this.locateRoutesFromDb());
        Map<String, ZuulProperties.ZuulRoute> newRouteMap = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            ZuulProperties.ZuulRoute value = entry.getValue();
            value.setId(entry.getKey());
            newRouteMap.put(value.getPath(), value);
        }
        return newRouteMap;
    }

    private Map<String, ZuulProperties.ZuulRoute> locateRoutesFromDb() {
        JSONObject configJson = JSON.parseObject(routeConfig);
        Map<String, ZuulProperties.ZuulRoute> routeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : configJson.entrySet()) {
            routeMap.put(entry.getKey(), configJson.getObject(entry.getKey(), ZuulProperties.ZuulRoute.class));
        }
        return routeMap;
    }

    public void setRouteConfig(String routeConfig) {
        this.routeConfig = routeConfig;
    }
}
