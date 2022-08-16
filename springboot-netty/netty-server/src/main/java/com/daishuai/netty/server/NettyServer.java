package com.daishuai.netty.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author admin
 * @version 1.0.0
 * @description netty实现服务端
 * @createTime 2022-08-09 21:39:47
 */
@SpringBootApplication
public class NettyServer {

    public static void main(String[] args) {
        SpringApplication.run(NettyServer.class, args);

    }
}
