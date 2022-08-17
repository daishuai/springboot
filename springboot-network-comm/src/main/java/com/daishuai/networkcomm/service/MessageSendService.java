package com.daishuai.networkcomm.service;

import com.daishuai.networkcomm.common.CommCache;
import com.daishuai.networkcomm.common.NettySession;
import com.daishuai.networkcomm.model.TcpMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @version 1.0.0
 * @description 消息发送器
 * @createTime 2022-08-17 22:23:37
 */
@Slf4j
@Component
public class MessageSendService {

    public void sendMessage(TcpMessageModel messageModel) {
        if (messageModel == null) {
            return;
        }
        String clientId = messageModel.getClientId();
        if (StringUtils.isBlank(clientId)) {
            for (NettySession session : CommCache.CHANNEL_MAP.values()) {
                session.getContext().channel().writeAndFlush(messageModel);
            }
        } else {
            NettySession session = CommCache.CLIENT_MAP.get(clientId);
            if (session != null) {
                session.getContext().channel().writeAndFlush(messageModel);
            } else {
                CommCache.CHANNEL_MAP.values().stream().filter(se -> StringUtils.isBlank(se.getClientId()))
                        .forEach(se -> se.getContext().channel().writeAndFlush(messageModel));
            }
        }
    }
}
