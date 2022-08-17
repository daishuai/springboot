package com.daishuai.networkcomm.handler;

import com.alibaba.fastjson.JSON;
import com.daishuai.networkcomm.common.SpringBeanFactoryUtils;
import com.daishuai.networkcomm.model.TcpMessageModel;
import com.daishuai.networkcomm.service.DispatchService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 服务端业务处理器
 * @createTime 2022年08月17日 13:39:00
 */
@Slf4j
public class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    private DispatchService dispatchService = SpringBeanFactoryUtils.getBean(DispatchService.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (!(msg instanceof ByteBuf)) {
                ctx.fireChannelRead(msg);
                release = false;
                return;
            }
            ByteBuf byteBuf = (ByteBuf) msg;
            String jsonStr = byteBuf.toString(StandardCharsets.UTF_8);
            TcpMessageModel tcpMessageModel = JSON.parseObject(jsonStr, TcpMessageModel.class);
            dispatchService.dispatch(ctx, tcpMessageModel);
        } finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }
        }

    }
}
