package com.daishuai.resource.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @author admin
 * @version 1.0.0
 * @description 拦截器配置
 * @createTime 2022-10-21 22:30:44
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Resource
    private HandlerInterceptor resourceAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(resourceAuthInterceptor).addPathPatterns("/**");
    }
}
