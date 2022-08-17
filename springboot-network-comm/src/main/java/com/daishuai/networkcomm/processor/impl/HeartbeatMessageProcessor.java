package com.daishuai.networkcomm.processor.impl;

import com.alibaba.fastjson.JSON;
import com.daishuai.networkcomm.model.TcpMessageModel;
import com.daishuai.networkcomm.processor.TcpMessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @version 1.0.0
 * @description 心跳
 * @createTime 2022-08-17 20:53:44
 */
@Slf4j
@Component
public class HeartbeatMessageProcessor implements TcpMessageProcessor {
    @Override
    public String type() {
        return "heartbeat";
    }

    @Override
    public void process(ChannelHandlerContext ctx, TcpMessageModel tcpMessage) {
        log.info("Heartbeat Message: {}", JSON.toJSONString(tcpMessage));
    }
}
