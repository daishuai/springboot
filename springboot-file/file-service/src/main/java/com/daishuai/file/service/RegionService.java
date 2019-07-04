package com.daishuai.file.service;

import com.daishuai.file.entity.RegionEntity;

/**
 * @author Daishuai
 * @description 行政区域
 * @date 2019/7/4 12:26
 */
public interface RegionService {
    
    /**
     * 根据code获取行政区划
     * @param code
     * @return
     */
    RegionEntity getRegionByCode(String code);
}
