package com.daishuai.jpa.service;

import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.jpa.entity.VehicleInfo;
import com.daishuai.jpa.repository.VehicleInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/7 17:08
 */
@Slf4j
@Service
public class VehicleInfoService {
    
    @Autowired
    private VehicleInfoDao vehicleInfoDao;
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    @Scheduled(cron = "0 0/1 * * * ?")
    public void matchVehicle() {
        List<VehicleInfo> vehicleInfos = vehicleInfoDao.findAll();
        log.info("总记录数：{}", vehicleInfos.size());
    }
    
    
}
