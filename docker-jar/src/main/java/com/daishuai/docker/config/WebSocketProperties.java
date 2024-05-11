package com.daishuai.docker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Daishuai
 * @description WebSocket Properties
 * @date 2019/8/10 11:04
 */
@ConfigurationProperties(prefix = "commons.websocket")
@Data
public class WebSocketProperties {
    
    /**
     * 节点
     */
    private String endPoint = "/ws/stomp";
    
    /**
     * 跨域支持
     */
    private String allowedOrigins = "*";
    
    /**
     * 订阅的主题
     */
    private String[] enableSimpleBroker = {"/user", "/queue", "/topic", "/client", "/app"};
    
    /**
     * 客户端发送消息时的前缀
     */
    private String applicationDestinationPrefixes = "/ws";
    
    /**
     * 默认的心跳发送间隔
     */
    private Long heartBeatInterval = 10000L;
}

