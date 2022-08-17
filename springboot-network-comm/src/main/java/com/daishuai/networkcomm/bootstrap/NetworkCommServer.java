package com.daishuai.networkcomm.bootstrap;

import com.daishuai.networkcomm.initializer.TcpServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 服务端
 * @createTime 2022年08月17日 14:00:00
 */
@Slf4j
public class NetworkCommServer implements DisposableBean, InitializingBean {

    private final int port;

    private final EventLoopGroup parentGroup;

    private final EventLoopGroup childGroup;

    public NetworkCommServer(int port) {
        this(port, null, null);
    }

    public NetworkCommServer(int port, EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        this.port = port;
        this.parentGroup = parentGroup == null ? new NioEventLoopGroup(1) : parentGroup;
        this.childGroup = childGroup == null ? new NioEventLoopGroup() : childGroup;
    }

    public void startServer() {
        log.info("启动服务端");
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new TcpServerChannelInitializer());
        ChannelFuture future;
        try {
            future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端启动失败: {}", e.getMessage(), e);
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public void stopServer() {
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

    @Override
    public void destroy() throws Exception {
        this.stopServer();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::startServer, "Network-Comm-Server").start();
    }
}
