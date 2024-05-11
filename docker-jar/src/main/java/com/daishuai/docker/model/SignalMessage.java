package com.daishuai.docker.model;

import lombok.Data;

/**
 * 信令消息
 *
 * @author daishuai
 * @date 2024/5/10 09:26:08
 */
@Data
public class SignalMessage {

    private String callDeviceId;

    private String callName;

    private String replyDeviceId;

    private String replyName;

    private Boolean isReceived;

    private String sessionDescription;

    private Object iceCandidate;

    private Integer msgType;
}
