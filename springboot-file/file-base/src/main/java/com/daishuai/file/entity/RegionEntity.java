package com.daishuai.file.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Daishuai
 * @description 行政区划
 * @date 2019/7/4 12:32
 */
@Data
public class RegionEntity {
    
    private Integer adcode;
    
    private String name;
    
    private List<RegionEntity> c;
}
