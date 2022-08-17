package com.daishuai.networkcomm.initializer;

import com.daishuai.networkcomm.encoder.TcpMessageEncoder;
import com.daishuai.networkcomm.handler.ConnectorIdleStateTrigger;
import com.daishuai.networkcomm.handler.DefaultChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description Tcp客户端Channel初始化器
 * @createTime 2022年08月17日 10:48:00
 */
public class TcpClientChannelInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
        pipeline.addLast(new ConnectorIdleStateTrigger());
        pipeline.addLast(new TcpMessageEncoder());
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new DefaultChannelHandler());
    }
}
