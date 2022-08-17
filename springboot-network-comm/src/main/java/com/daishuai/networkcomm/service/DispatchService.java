package com.daishuai.networkcomm.service;

import com.daishuai.networkcomm.model.TcpMessageModel;
import com.daishuai.networkcomm.processor.TcpMessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 消息分发器
 * @createTime 2022年08月17日 13:22:00
 */
@Slf4j
@Component
public class DispatchService {

    private Map<String, TcpMessageProcessor> processorMap;

    @Autowired(required = false)
    public void setProcessorMap(List<TcpMessageProcessor> processors) {
        processorMap = new HashMap<>();
        if (CollectionUtils.isEmpty(processors)) {
            return;
        }
        for (TcpMessageProcessor processor : processors) {
            processorMap.put(processor.type(), processor);
        }
    }

    public void dispatch(ChannelHandlerContext ctx, TcpMessageModel tcpMessage) {
        String type = tcpMessage.getType();
        if (processorMap == null) {
            log.warn("没有配置消息处理器, 该消息将被丢弃");
            return;
        }
        processorMap.computeIfPresent(type, (messageType, tcpMessageProcessor) -> {
            try {
                tcpMessageProcessor.process(ctx, tcpMessage);
            } catch (Exception e) {
                tcpMessageProcessor.onError(e);
            }
            return tcpMessageProcessor;
        });
    }

}
