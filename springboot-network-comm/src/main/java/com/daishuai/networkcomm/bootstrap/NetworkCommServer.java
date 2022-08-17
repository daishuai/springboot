package com.daishuai.networkcomm.bootstrap;

import com.daishuai.networkcomm.encoder.TcpMessageEncoder;
import com.daishuai.networkcomm.handler.AcceptorIdleStateTrigger;
import com.daishuai.networkcomm.handler.DefaultChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 服务端
 * @createTime 2022年08月17日 14:00:00
 */
@Slf4j
public class NetworkCommServer implements DisposableBean, InitializingBean {

    private final int port;

    private final long readIdleTime;

    private final EventLoopGroup parentGroup;

    private final EventLoopGroup childGroup;

    public NetworkCommServer(int port, long readIdleTime) {
        this(port, readIdleTime, null, null);
    }

    public NetworkCommServer(int port, long readIdleTime, EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        this.port = port;
        this.readIdleTime = readIdleTime;
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
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        pipeline.addLast(new IdleStateHandler(readIdleTime, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new TcpMessageEncoder());
                        pipeline.addLast(new AcceptorIdleStateTrigger());
                        pipeline.addLast(new JsonObjectDecoder());
                        pipeline.addLast(new DefaultChannelHandler());
                    }
                });
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
