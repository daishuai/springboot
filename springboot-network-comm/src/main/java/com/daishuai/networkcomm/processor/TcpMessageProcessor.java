package com.daishuai.networkcomm.processor;

import com.daishuai.networkcomm.model.TcpMessageModel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 消息处理器
 * @createTime 2022年08月17日 13:23:00
 */
public interface TcpMessageProcessor {

    Logger logger = LoggerFactory.getLogger(TcpMessageProcessor.class);

    /**
     * 消息类型
     *
     * @return  消息类型
     */
    String type();

    /**
     * 处理tcp消息
     *
     * @param ctx 通道
     * @param tcpMessage    tcp消息
     */
    void process(ChannelHandlerContext ctx, TcpMessageModel tcpMessage);


    default void onError(Throwable throwable) {
        logger.info("Process TcpMessage On Error: {}", throwable.getMessage(), throwable);
    }
}
