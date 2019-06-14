package com.daishuai.es.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Daishuai
 * @description 默认处理器
 * @date 2019/5/28 11:47
 */
@Slf4j
public class DefaultEsFailureHandler implements EsFailureHandler {
    @Override
    public void handleFailure(String index, String type, String id, String opType, Map<String, Object> doc, Throwable failure) {
        log.error("index:{}, type:{}, id:{}, opType:{}, doc:{}, failureMessage:{}", index, type, id, opType, JSON.toJSONString(doc), failure.getMessage(), failure);
    }
}
