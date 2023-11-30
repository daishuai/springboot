package com.daishuai.demo.config;

import com.daishuai.demo.mapper.JobConfigMapper;
import com.daishuai.demo.model.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @version 1.0.0
 * @description 配置初始化
 * @createTime 2023年11月30日 14:08:00
 */
@Slf4j
@Configuration
public class ConfigBeforeInit {

    @Resource
    private ConfigurableEnvironment environment;

    @Resource
    private JobConfigMapper jobConfigMapper;

    @PostConstruct
    public void initSystemConfig() {
        // 获取系统属性集合
        MutablePropertySources propertySources = environment.getPropertySources();
        // 从数据库获取配置
        Map<String, String> configMap = jobConfigMapper.queryByType(3).stream().collect(Collectors.toMap(SystemConfig::getCode, SystemConfig::getValue));
        // 将转换后的列表加入属性中
        Properties properties = new Properties();
        properties.putAll(configMap);
        // 将属性转换为属性集合，并指定名称
        PropertiesPropertySource propertySource = new PropertiesPropertySource("system-config", properties);
        // 定义寻找属性的正则，该正则为系统默认属性集合的前缀
        Pattern p = Pattern.compile("^applicationConfig.*");
        // 接收系统默认属性集合的名称
        String name = null;
        // 标识是否找到系统默认属性集合
        boolean flag = false;
        // 遍历属性集合
        for (PropertySource<?> source : propertySources) {
            // 正则匹配  匹配到：OriginTrackedMapPropertySource {name='applicationConfig: [classpath:/application.properties]'}
            if (p.matcher(source.getName()).matches()) {
                // 接收名称
                name = source.getName();
                // 变更标识
                flag = true;

                break;
            }
        }
        if (flag) {
            // 找到则将自定义属性添加到该属性之后，意思就是以application.properties文件配置为准  如果想要以数据库配置为准，就修改为 propertySources.addBefore(name, constants)
            propertySources.addBefore(name, propertySource);
        } else {
            // 没找到默认添加到最后
            propertySources.addFirst(propertySource);
        }

    }
}
