package com.daishuai.excel.constant;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 14:54
 */
public class CommonCache {
    
    public static final ConcurrentMap<String, Object> downloadProgress = new ConcurrentHashMap<>();
}
