package com.daishuai.es.config;

import lombok.Data;

/**
 * @author wangkaiqi
 * @date Created in 2019/4/12 17:40
 */
@Data
public class ElasticsearchNodeInfo {
    private String clusterName;
    private String restNodes;
    private String username;
    private String password;
    private Integer threadNum=1;
    private Integer connectTimeout=3000;
}