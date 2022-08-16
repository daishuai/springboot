package com.daishuai.netty.server.model;

import lombok.Data;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description TCP消息
 * @createTime 2022年08月16日 18:08:00
 */
@Data
public class TcpMessageModel {

    private String type;

    private String id;

    private Object content;

    private Integer ack;
}
