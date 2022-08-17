package com.daishuai.networkcomm.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.StandardCharsets;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 客户端心跳, 客户端长时间没有向服务端写数据，触发
 * @createTime 2022年08月17日 10:55:00
 */
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("{\"type1\":\"heartbeat\"}", StandardCharsets.UTF_8));
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
