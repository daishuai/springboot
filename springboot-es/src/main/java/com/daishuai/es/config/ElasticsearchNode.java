package com.daishuai.es.config;

import lombok.Data;

/**
 * @author admin
 * @version 1.0.0
 * @description ES节点
 * @createTime 2022-08-07 17:32:16
 */
@Data
public class ElasticsearchNode {

    /**
     * ES集群名称
     */
    private String clusterName;

    /**
     * ES节点ip:port, 例如：10.65.3.17:9200, 10.65.3.18:9200
     */
    private String restNodes;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否开启嗅探
     */
    private Boolean sniff = false;

    /**
     * 连接线程数
     */
    private Integer threadNum = 1;

    /**
     * 创建连接及请求的超时时间
     */
    private Integer connectTimeout = 3000;

    private Integer bulkActions = 20;

    private Integer bulkSize = 5;

    private Integer flushInterval = 5;

    private Integer concurrentRequests = 5;

}
