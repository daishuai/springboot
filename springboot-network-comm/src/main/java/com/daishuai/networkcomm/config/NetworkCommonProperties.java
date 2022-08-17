package com.daishuai.networkcomm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 配置信息
 * @createTime 2022年08月17日 16:24:00
 */
@Data
@ConfigurationProperties(prefix = "kem.suite.network-comm")
public class NetworkCommonProperties {

    private String host;

    private int port;

    private long readIdleTime;

    private long writeIdleTime;

}
