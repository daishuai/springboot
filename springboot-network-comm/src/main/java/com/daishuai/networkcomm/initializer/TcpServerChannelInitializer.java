package com.daishuai.networkcomm.initializer;

import com.daishuai.networkcomm.encoder.TcpMessageEncoder;
import com.daishuai.networkcomm.handler.AcceptorIdleStateTrigger;
import com.daishuai.networkcomm.handler.DefaultChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description Tcp服务端Channel初始化器
 * @createTime 2022年08月17日 10:14:00
 */
public class TcpServerChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new TcpMessageEncoder());
        pipeline.addLast(new AcceptorIdleStateTrigger());
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new DefaultChannelHandler());
    }
}
