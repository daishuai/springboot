package com.daishuai.netty.server.handler;

import com.alibaba.fastjson.JSON;
import com.daishuai.netty.server.common.CommonCache;
import com.daishuai.netty.server.model.TcpMessageModel;
import com.daishuai.netty.server.service.DispatchService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 服务端处理器
 * @createTime 2022年08月16日 18:12:00
 */
@Slf4j
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private DispatchService dispatchService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelId channelId = ctx.channel().id();
        CommonCache.CLIENT_MAP.put(channelId.asLongText(), ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            return;
        }
        ByteBuf byteBuf = (ByteBuf) msg;
        String jsonStr = byteBuf.toString(StandardCharsets.UTF_8);
        TcpMessageModel messageModel = JSON.parseObject(jsonStr, TcpMessageModel.class);
        dispatchService.dispatch(ctx, messageModel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelId channelId = ctx.channel().id();
        CommonCache.CLIENT_MAP.remove(channelId.asLongText());
    }
}
