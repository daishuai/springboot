package com.daishuai.kafka.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author Daishuai
 * @description 消息体
 * @date 2019/6/15 18:59
 */
@Data
public class MessageEntity {

    /**
     * 消息id
     */
    private String uuid;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 发送时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
}
