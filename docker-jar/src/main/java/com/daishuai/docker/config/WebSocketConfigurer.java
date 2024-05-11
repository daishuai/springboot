package com.daishuai.docker.config;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Daishuai
 * @description WebSocket Config
 * @date 2019/8/10 11:03
 */
@Configuration
@EnableConfigurationProperties(value = {WebSocketProperties.class})
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfigurer extends AbstractWebSocketMessageBrokerConfigurer {
    
    @Resource
    private WebSocketProperties webSocketProperties;

    @Autowired(required = false)
    private List<ChannelInterceptor> channelInterceptors;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 注册一个Stomp的节点（endpoint）,并指定使用SockJS协议。
        stompEndpointRegistry
                .addEndpoint(webSocketProperties.getEndPoint())
                .setAllowedOrigins(webSocketProperties.getAllowedOrigins())
                .withSockJS();
        stompEndpointRegistry.setErrorHandler(new StompSubProtocolErrorHandler());
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        
        // 定义心跳线程
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        taskScheduler.setDaemon(true);
        taskScheduler.initialize();
        
        // 服务端发送消息给客户端的域,多个用逗号隔开
        registry.enableSimpleBroker(webSocketProperties.getEnableSimpleBroker())
                // 定义心跳间隔 单位(ms)
                .setHeartbeatValue(new long[]{webSocketProperties.getHeartBeatInterval(), webSocketProperties.getHeartBeatInterval()})
                .setTaskScheduler(taskScheduler);
        // 定义webSocket前缀
        registry.setApplicationDestinationPrefixes(webSocketProperties.getApplicationDestinationPrefixes());
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        if (CollectionUtils.isEmpty(channelInterceptors)) {
            return;
        }
        registration.interceptors(channelInterceptors.toArray(new ChannelInterceptor[0]));
    }

}
