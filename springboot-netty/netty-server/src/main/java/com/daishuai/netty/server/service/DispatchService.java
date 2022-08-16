package com.daishuai.netty.server.service;

import com.alibaba.fastjson.JSON;
import com.daishuai.netty.server.model.TcpMessageModel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 分发器
 * @createTime 2022年08月16日 19:30:00
 */
@Slf4j
@Component
public class DispatchService {

    private Map<String, DataProcessor> dataProcessorMap;

    @Autowired(required = false)
    public void setDataProcessorMap(List<DataProcessor> processors) {
        dataProcessorMap = new HashMap<>();
        if (CollectionUtils.isEmpty(processors)) {
            return;
        }
        for (DataProcessor processor : processors) {
            dataProcessorMap.put(processor.type(), processor);
        }
    }

    public void dispatch(ChannelHandlerContext ctx, TcpMessageModel messageModel) {
        log.info("收到客户端的消息: {}", JSON.toJSONString(messageModel));
        String type = messageModel.getType();
        dataProcessorMap.computeIfPresent(type, (s, dataProcessor) -> {
            try {
                dataProcessor.process(ctx, messageModel);
            } catch (Exception e) {
                dataProcessor.onError(e);
            }
            return dataProcessor;
        });
    }
}
