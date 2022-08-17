package com.daishuai.networkcomm.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @version 1.0.0
 * @description Bean工具类
 * @createTime 2022-08-16 21:47:38
 */
@Component
public class SpringBeanFactoryUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanFactoryUtils.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> tClass) {
        return applicationContext == null ? null : applicationContext.getBean(tClass);
    }
}
