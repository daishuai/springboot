package com.daishuai.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wangkaiqi
 * @date Created in 2019/4/12 17:07
 */

@ConfigurationProperties(prefix = "spring.elasticsearch")
@Component
@Data
public class EsConfig {
    Map<String,ElasticsearchNodeInfo> clusters;
    String defaultCluster;
}
