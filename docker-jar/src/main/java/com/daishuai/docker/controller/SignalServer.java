package com.daishuai.docker.controller;

import com.alibaba.fastjson.JSON;
import com.daishuai.docker.model.SignalMessage;
import com.daishuai.docker.vo.SignalResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 信令服务器
 *
 * @author daishuai
 * @date 2024/5/10 09:14:40
 */
@Slf4j
@RestController
public class SignalServer {

    private static final String SIGNAL_SERVER_TOPIC_FORMAT = "/user/voice/{}";

    private final Set<String> clients = new HashSet<>();

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping(value = "/app/voice/{code}")
    @SendTo(value = "/user/voice/{code}")
    public SignalResponse proxy(@DestinationVariable(value = "code") String code, @RequestBody SignalMessage message) {
        log.info("message: {}", JSON.toJSONString(message));
        Integer msgType = message.getMsgType();
        if (msgType == null) {
            return new SignalResponse(-1, "消息类型不能为空");
        }
        if (msgType == 0) {
            // 客户端注册
            clients.add(code);
            return new SignalResponse(1, "成功");
        }
        if (msgType == 8) {
            // 取消注册
            clients.remove(code);
            return new SignalResponse(1, "成功");
        }
        String replyDeviceId = message.getReplyDeviceId();
        if (StringUtils.isBlank(replyDeviceId)) {
            return new SignalResponse(-1, "接收方ID不能为空");
        }
        String callDeviceId = message.getCallDeviceId();
        if (StringUtils.isBlank(callDeviceId)) {
            return new SignalResponse(-1, "发起方ID不能为空");
        }
        if (!Boolean.TRUE.equals(clients.contains(callDeviceId))) {
            return new SignalResponse(2, "本方未注册");
        }
        if (!Boolean.TRUE.equals(clients.contains(replyDeviceId))) {
            return new SignalResponse(3, "对方未注册");
        }
        simpMessagingTemplate.convertAndSend(StringUtils.replace(SIGNAL_SERVER_TOPIC_FORMAT, "{}", replyDeviceId), message);
        return new SignalResponse(1, "成功");
    }
}
