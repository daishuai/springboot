package com.daishuai.networkcomm.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 连接空闲监控, 长时间没有读取到客户端发送的数据
 * @createTime 2022年08月17日 13:10:00
 */
@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {

            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
