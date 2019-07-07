package com.daishuai.weather.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Daishuai
 * @description 城市
 * @date 2019/7/4 23:31
 */
@Data
public class CityEntity {
    
    /**
     * 城市编码
     */
    private String code;
    
    /**
     * 城市名称
     */
    private String name;
    
    /**
     * 下属城市
     */
    private List<CityEntity> children;
}
