package com.daishuai.es.controller;

import com.daishuai.es.enums.CommonEsAliases;
import com.daishuai.es.service.ElasticsearchApi;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * @author admin
 * @version 1.0.0
 * @description 测试
 * @createTime 2022-08-07 22:24:19
 */
@RestController
public class EsDemoController {

    @Resource
    private ElasticsearchApi defaultElasticsearchApi;

    @Resource
    private ElasticsearchApi localElasticsearchApi;

    @GetMapping(value = "/bulkProcess")
    public Object bulkProcess() {
        MatchAllQueryBuilder allQuery = matchAllQuery();
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(allQuery)
                .size(200);
        SearchResponse searchResponse = defaultElasticsearchApi.searchData(CommonEsAliases.ZQXX, source);
        int count = 0;
        for (SearchHit hit : searchResponse.getHits()) {
            count += 1;
            localElasticsearchApi.upsertToProcessor(hit.getIndex(), hit.getType(), count == 32 ? "" : hit.getId(), hit.getSourceAsString());
        }
        return searchResponse;
    }
}
