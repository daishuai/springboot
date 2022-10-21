package com.daishuai.resource.config;

import com.daishuai.resource.filter.ResourceAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author admin
 * @version 1.0.0
 * @description 过滤器配置
 * @createTime 2022-10-21 22:06:33
 */
@Configuration
public class FilterConfig {

    @Resource
    private ResourceAuthFilter resourceAuthFilter;

    @Bean
    public FilterRegistrationBean registerResourceAuthFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(resourceAuthFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("resourceAuthFilter");
        return registrationBean;
    }
}
