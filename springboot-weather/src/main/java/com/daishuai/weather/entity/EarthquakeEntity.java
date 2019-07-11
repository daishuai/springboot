package com.daishuai.weather.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author Daishuai
 * @description 地震
 * @date 2019/7/9 15:56
 */
@Data
public class EarthquakeEntity {
    
    private String dateType;
    
    /**
     * 深度(km)
     */
    private String depth;
    
    /**
     * 地震中心
     */
    private String epicenter;
    
    private String id;
    
    /**
     * 纬度
     */
    private String latitudes;
    
    /**
     * 经度
     */
    private String longitudes;
    
    /**
     * 震级
     */
    private String num_mag;
    
    /**
     * 发震时刻
     */
    private String orig_time;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 发布时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;
    
}
