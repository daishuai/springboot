package com.daishuai.networkcomm.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @version 1.0.0
 * @description 缓存
 * @createTime 2022-08-17 22:26:35
 */
public class CommCache {

    /**
     * 客户端通道缓存
     */
    public static final Map<String, NettySession> CLIENT_MAP = new ConcurrentHashMap<>();

    public static final Map<String, NettySession> CHANNEL_MAP = new ConcurrentHashMap<>();


}
