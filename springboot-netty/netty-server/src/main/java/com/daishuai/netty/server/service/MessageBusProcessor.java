package com.daishuai.netty.server.service;

import com.alibaba.fastjson.JSONObject;
import com.daishuai.netty.server.model.TcpMessageModel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 消息处理器
 * @createTime 2022年08月16日 19:44:00
 */
@Slf4j
@Component
public class MessageBusProcessor implements DataProcessor{
    @Override
    public String type() {
        return "messageBus";
    }

    @Override
    public void process(ChannelHandlerContext ctx, TcpMessageModel messageModel) {
        Integer ack = messageModel.getAck();
        if (ack != null && ack == 1) {
            JSONObject json = new JSONObject();
            json.put("id", messageModel.getId());
            ctx.channel().writeAndFlush(json.toJSONString());
        }
    }
}
