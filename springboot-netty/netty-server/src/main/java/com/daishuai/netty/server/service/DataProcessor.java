package com.daishuai.netty.server.service;

import com.daishuai.netty.server.model.TcpMessageModel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 数据处理
 * @createTime 2022年08月16日 19:31:00
 */
public interface DataProcessor {

    String type();

    void process(ChannelHandlerContext ctx, TcpMessageModel messageModel);

    default void onError(Throwable e) {
        e.printStackTrace();
    }
}
