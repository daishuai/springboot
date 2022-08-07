package com.daishuai.es.config;

import com.daishuai.es.factory.RestEsApiFactory;
import com.daishuai.es.service.ElasticsearchApi;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindException;

import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description ES配置信息
 * @createTime 2022-08-07 17:44:13
 */
@EnableConfigurationProperties(value = ElasticsearchProperties.class)
@Configuration
public class ElasticsearchConfig implements BeanFactoryPostProcessor, EnvironmentAware {

    private ConfigurableEnvironment environment;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ElasticsearchProperties properties = this.resolveSettings();
        Map<String, ElasticsearchNode> clusters = properties.getClusters();
        if (MapUtils.isEmpty(clusters)) {
            return;
        }
        for (Map.Entry<String, ElasticsearchNode> nodeEntry : clusters.entrySet()) {
            String key = nodeEntry.getKey();
            ElasticsearchNode node = nodeEntry.getValue();
            ElasticsearchApi elasticsearchApi = RestEsApiFactory.buildEsApi(node);
            if (elasticsearchApi == null) {
                continue;
            }
            beanFactory.registerSingleton(key + "ElasticsearchApi", elasticsearchApi);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    private ElasticsearchProperties resolveSettings() {
        ElasticsearchProperties settings = new ElasticsearchProperties();
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(settings);
        factory.setTargetName("spring.elasticsearch");
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());
        try {
            factory.bindPropertiesToTarget();
        } catch (BindException ex) {
            throw new FatalBeanException("Could not bind Kafka Second properties", ex);
        }
        return settings;
    }
}
