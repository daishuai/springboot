package com.daishuai.es.controller;

import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.es.enums.EsAliases;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/6/12 13:33
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private RestElasticsearchApi restElasticsearchApi;


    @GetMapping("/get")
    public Object getIncident() {
        BoolQueryBuilder boolQuery = boolQuery();
        boolQuery.mustNot(termQuery("JLZT", "0"));
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(boolQuery);
        SearchRequest request = new SearchRequest()
                .indices(EsAliases.ZQXX.getIndex())
                .types(EsAliases.ZQXX.getType())
                .source(source);
        SearchResponse searchResponse = restElasticsearchApi.getSearchResponse(request);
        return searchResponse.getHits().getHits();
    }
}
