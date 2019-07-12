package com.daishuai.weather.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 21:56
 */
@Data
public class IncidentEntity {
    
    private String id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 发布时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;
    
    /**
     * 发布来源
     */
    private String datasource;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 地址
     */
    private String url;
}
