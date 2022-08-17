package com.daishuai.networkcomm.config;

import com.daishuai.networkcomm.bootstrap.NetWorkCommClient;
import com.daishuai.networkcomm.bootstrap.NetworkCommServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 配置
 * @createTime 2022年08月17日 14:49:00
 */
@Configuration
public class NetworkCommConfig {


    @Bean
    @ConditionalOnProperty(name = "kem.suite.network-comm.mode", havingValue = "server")
    public NetworkCommServer networkCommServer() {
        return new NetworkCommServer(8889);
    }

    @Bean
    @ConditionalOnProperty(name = "kem.suite.network-comm.mode", havingValue = "client")
    public NetWorkCommClient netWorkCommClient() {
        return new NetWorkCommClient("localhost", 8889);
    }
}
