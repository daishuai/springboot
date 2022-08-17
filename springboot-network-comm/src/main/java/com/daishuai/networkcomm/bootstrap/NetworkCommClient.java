package com.daishuai.networkcomm.bootstrap;

import com.daishuai.networkcomm.encoder.TcpMessageEncoder;
import com.daishuai.networkcomm.handler.ConnectionWatchDog;
import com.daishuai.networkcomm.handler.ConnectorIdleStateTrigger;
import com.daishuai.networkcomm.handler.DefaultChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 客户端
 * @createTime 2022年08月17日 14:00:00
 */
@Slf4j
public class NetworkCommClient implements DisposableBean, InitializingBean {

    private final String host;

    private final int port;

    private final long writeIdleTime;

    private final EventLoopGroup group;

    protected final HashedWheelTimer timer = new HashedWheelTimer();

    public NetworkCommClient(String host, int port, long writeIdleTime) {
        this(host, port, writeIdleTime, null);
    }

    public NetworkCommClient(String host, int port, long writeIdleTime, EventLoopGroup group) {
        this.host = host;
        this.port = port;
        this.writeIdleTime = writeIdleTime;
        this.group = group == null ? new NioEventLoopGroup() : group;
    }

    public void startClient() {
        log.info("启动客户端");
        Bootstrap bootstrap = new Bootstrap();
        ConnectionWatchDog watchDog = new ConnectionWatchDog(bootstrap, timer, host, port, true) {

            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[] {
                        this,
                        new LoggingHandler(LogLevel.INFO),
                        new IdleStateHandler(0 , writeIdleTime, 0, TimeUnit.SECONDS),
                        new ConnectorIdleStateTrigger(),
                        new TcpMessageEncoder(),
                        new JsonObjectDecoder(),
                        new DefaultChannelHandler()
                };
            }
        };
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(watchDog.handlers());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("客户端启动失败: {}", e.getMessage());
            timer.newTimeout(watchDog, 2, TimeUnit.SECONDS);
        }
    }

    public void stopClient() {
        group.shutdownGracefully();
    }

    @Override
    public void destroy() throws Exception {
        this.stopClient();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::startClient, "Network-Comm-Client").start();
    }
}
