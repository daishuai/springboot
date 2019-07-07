package com.daishuai.weather.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.daishuai.weather.entity.CityEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Daishuai
 * @description 公共缓存
 * @date 2019/7/4 23:21
 */
@Slf4j
@Component
public class CommonCache {
    
    @Value("${city-code.file-path:classpath:CityCode.json}")
    private String filePath = "classpath:CityCode.json";
    
    public static final Map<String, String> cityMap = new HashMap<>();
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @PostConstruct
    public void init() {
        this.initCityData();
    }
    
    /**
     * 初始化城市数据
     */
    private void initCityData() {
        Resource resource = resourceLoader.getResource(filePath);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            log.error("读取城市代码文件出错：{}", e.getMessage(), e);
        }
        Scanner scanner = new Scanner(inputStream);
        StringBuilder temp = new StringBuilder();
        while (scanner.hasNextLine()) {
            temp.append(scanner.nextLine());
        }
        JSONObject jsonObject = JSON.parseObject(temp.toString());
        JSONArray array = jsonObject.getJSONArray("cities");
        List<CityEntity> cities = array.toJavaList(CityEntity.class);
        this.cacheCity(cities);
        log.info("缓存城市数量：{}", cityMap.size());
    }
    
    /**
     * 递归缓存城市数据
     * @param cities
     */
    private void cacheCity(List<CityEntity> cities) {
        if (CollectionUtils.isNotEmpty(cities)) {
            cities.forEach(cityEntity -> {
                String code = cityEntity.getCode();
                List<CityEntity> children = cityEntity.getChildren();
                if (StringUtils.isNotEmpty(code)) {
                    cityMap.put(code, cityEntity.getName());
                }
                this.cacheCity(children);
            });
        }
    }
}
