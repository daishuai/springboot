package com.daishuai.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.es.entity.EmployeeEntity;
import com.daishuai.es.enums.EsAliases;
import com.daishuai.es.service.EmployeeService;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Daishuai
 * @description 雇员
 * @date 2019/6/7 12:59
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private RestElasticsearchApi restElasticsearchApi;

    @Override
    public EmployeeEntity findById(String id) {
        GetResponse getResponse = restElasticsearchApi.getDataById(EsAliases.EMPLOYEE.getIndex(), EsAliases.EMPLOYEE.getType(), id);
        String sourceAsString = getResponse.getSourceAsString();
        return JSON.parseObject(sourceAsString, EmployeeEntity.class);
    }
}
