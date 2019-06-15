package com.daishuai.es.controller;

import com.daishuai.common.entity.ResponseEntity;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.es.enums.EsAliases;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Daishuai
 * @description 测试用例
 * @date 2019/6/12 13:33
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private RestElasticsearchApi restElasticsearchApi;


    @GetMapping("/get")
    public ResponseEntity getData(String clusterName, String id) {
        EsAliases esAliases = this.getEsAliases(clusterName);
        if (esAliases == null) {
            return ResponseEntity.error("集群索引不存在");
        }
        GetResponse response = restElasticsearchApi.getGetResponse(new GetRequest(esAliases.getIndex(), esAliases.getType(), id));
        return ResponseEntity.success(response.getSource());
    }

    private EsAliases getEsAliases(String clusterName) {
        EsAliases[] values = EsAliases.values();
        Optional<EsAliases> aliases = Arrays.stream(values).filter(value -> StringUtils.contains(value.getIndex(), clusterName)).findFirst();
        return aliases.isPresent() ? aliases.get() : null;
    }
}
