package com.daishuai.netty.server.initializer;

import com.daishuai.netty.server.encoder.ObjectEncoder;
import com.daishuai.netty.server.handler.ServerChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 服务端Channel初始化器
 * @createTime 2022年08月16日 18:04:00
 */
public class ServerChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // JSON解码器
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new ServerChannelHandler());
    }
}
