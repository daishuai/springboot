package com.daishuai.networkcomm.handler;

import com.daishuai.networkcomm.initializer.TcpClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 重连检测
 * @createTime 2022年08月17日 10:30:00
 */
@Slf4j
@ChannelHandler.Sharable
public abstract class ConnectionWatchDog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    private final Bootstrap bootstrap;

    private final Timer timer;

    private final String host;
    private final int port;

    private volatile boolean reconnect = true;

    private int attempts;

    public ConnectionWatchDog(Bootstrap bootstrap, Timer timer, String host, int port, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.host = host;
        this.port = port;
        this.reconnect = reconnect;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("与服务端建立连接");
        attempts = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("与服务端断开连接");
        if (reconnect) {
            if (attempts < 12) {
                attempts ++;
                log.info("第{}次尝试与服务端重新连接", attempts);
                timer.newTimeout(this, 10, TimeUnit.SECONDS);
            }
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        log.info("尝试重新连接客户端");
        ChannelFuture future;
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                log.info("建立连接失败");
                channelFuture.channel().pipeline().fireChannelInactive();
            } else {
                log.info("与服务端重新建立连接");
            }
        });
    }
}
