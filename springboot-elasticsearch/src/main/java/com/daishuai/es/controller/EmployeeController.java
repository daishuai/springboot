package com.daishuai.es.controller;

import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.es.entity.EmployeeEntity;
import com.daishuai.es.enums.EsAliases;
import com.daishuai.es.service.EmployeeService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author Daishuai
 * @description 雇员
 * @date 2019/6/7 13:09
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;

    @GetMapping("/{id}")
    public EmployeeEntity getEmployeeById(@PathVariable("id") String id) {
        return employeeService.findById(id);
    }

    @GetMapping("/get")
    public Object getAll() {
        BoolQueryBuilder boolQuery = boolQuery();
        boolQuery.must(termQuery("first_name", "jane"));
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(boolQuery);
        SearchRequest request = new SearchRequest()
                .indices(EsAliases.EMPLOYEE.getIndex(), "megacorp")
                .types(EsAliases.EMPLOYEE.getType())
                .source(source);
        List<Map<String, Object>> result = new ArrayList<>();
        SearchResponse searchResponse = restElasticsearchApi.getSearchResponse(request);
        Arrays.stream(searchResponse.getHits().getHits()).forEach(searchHit -> result.add(searchHit.getSource()));
        return result;
    }
}
