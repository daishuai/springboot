package com.daishuai.file.controller;

import com.daishuai.file.entity.RegionEntity;
import com.daishuai.file.entity.ResponseEntity;
import com.daishuai.file.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daishuai
 * @description 行政区划
 * @date 2019/7/4 12:26
 */
@RestController
@RequestMapping(value = "/region")
public class RegionController {
    
    @Autowired
    private RegionService regionService;
    
    
    @GetMapping("/{code}")
    public ResponseEntity getRegionByCode(@PathVariable("code") String code) {
        RegionEntity regionEntity = regionService.getRegionByCode(code);
        return ResponseEntity.success(regionEntity);
    }
    
}
