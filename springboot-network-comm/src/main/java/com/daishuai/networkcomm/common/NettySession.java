package com.daishuai.networkcomm.common;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author admin
 * @version 1.0.0
 * @description
 * @createTime 2022-08-17 22:32:54
 */
@Data
public class NettySession {

    private String clientId;

    private String channelId;

    private ChannelHandlerContext context;
}
