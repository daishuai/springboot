package com.daishuai.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.daishuai.file.entity.RegionEntity;
import com.daishuai.file.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Daishuai
 * @description 行政区划
 * @date 2019/7/4 12:27
 */
@Slf4j
@Service
public class RegionServiceImpl implements RegionService {
    
    @Value("classpath:static/AreaCode.json")
    private Resource resource;
    
    @Value("${file-path:classpath:static/AreaCode.json}")
    private String filePath;
    
    private List<RegionEntity> regions;
    
    @PostConstruct
    public void init() {
        StringBuilder temp = new StringBuilder();
        File file = null;
        try {
            file = resource.getFile();
        } catch (IOException e) {
            log.error("读取文件异常：{}", e.getMessage(), e);
        }
        try (Scanner scanner = new Scanner(file, "UTF-8");) {
            while (scanner.hasNextLine()) {
                temp.append(scanner.nextLine());
            }
        } catch (IOException e) {
            log.error("读取文件异常：{}", e.getMessage(), e);
        }
        regions = JSON.parseArray(temp.toString(), RegionEntity.class);
    }
    
    @Override
    public RegionEntity getRegionByCode(String code) {
        
        return getRegion(regions, code);
    }
    
    /**
     * 递归匹配
     * @param regionEntities
     * @param code
     * @return
     */
    private RegionEntity getRegion(List<RegionEntity> regionEntities, String code) {
        if (CollectionUtils.isNotEmpty(regionEntities)) {
            Optional<RegionEntity> first = regionEntities.stream().filter(regionEntity -> StringUtils.equals(regionEntity.getAdcode().toString(), code)).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
            for (RegionEntity regionEntity : regionEntities) {
                RegionEntity region = getRegion(regionEntity.getC(), code);
                if (region != null) {
                    return region;
                }
            }
        }
        return null;
    }
}
