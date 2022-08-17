package com.daishuai.networkcomm.model;

import lombok.Data;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description Tcp消息
 * @createTime 2022年08月17日 10:18:00
 */
@Data
public class TcpMessageModel {

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息唯一标识
     */
    private String id;

    /**
     * 是否需要消息确认，ack=1,需要，否则不需要
     */
    private Integer ack;

    /**
     * 消息内容
     */
    private Object content;

    /**
     * 消息来源
     */
    private String source;
}
