package com.daishuai.networkcomm.handler;

import io.netty.channel.ChannelHandler;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description ChannelHandler集合
 * @createTime 2022年08月17日 16:35:00
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] handlers();
}
