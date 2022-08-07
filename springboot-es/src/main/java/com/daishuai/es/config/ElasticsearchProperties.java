package com.daishuai.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description ES配置信息
 * @createTime 2022-08-07 17:42:34
 */
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticsearchProperties {

    private Map<String, ElasticsearchNode> clusters;

    private String defaultCluster;
}
