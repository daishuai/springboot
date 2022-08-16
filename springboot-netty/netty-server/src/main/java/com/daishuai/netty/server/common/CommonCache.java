package com.daishuai.netty.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 通用缓存
 * @createTime 2022年08月16日 18:10:00
 */
public class CommonCache {

    public static final Map<String, ChannelHandlerContext> CLIENT_MAP = new ConcurrentHashMap<>();

    /**
     * 未知的客户端
     */
    public static final Set<ChannelHandlerContext> UNKNOWN_CLIENT_MAP = new CopyOnWriteArraySet<>();
}
