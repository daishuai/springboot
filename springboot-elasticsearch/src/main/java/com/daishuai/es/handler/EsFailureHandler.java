package com.daishuai.es.handler;

import java.util.Map;

/**
 * @author Daishuai
 * @description 失败处理器
 * @date 2019/5/28 11:24
 */
public interface EsFailureHandler {

    /**
     * 失败处理器
     * @param index
     * @param type
     * @param id
     * @param opType
     * @param doc
     * @param failure
     */
    void handleFailure(String index, String type, String id, String opType, Map<String, Object> doc, Throwable failure);
}
