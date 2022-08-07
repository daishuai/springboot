package com.daishuai.es.service;

import com.daishuai.es.enums.EsAliases;
import com.daishuai.es.factory.ElasticsearchRestClient;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description REST方式
 * @createTime 2022-05-27 14:18:00
 */
@Slf4j
public class RestElasticsearchApi implements ElasticsearchApi {

    private final ElasticsearchRestClient esClient;

    private final BulkProcessor bulkProcessor;

    public RestElasticsearchApi(ElasticsearchRestClient restClient, BulkProcessor processor) {
        this.esClient = restClient;
        this.bulkProcessor = processor;
    }

    @Override
    public SearchResponse searchData(EsAliases esAliases, SearchSourceBuilder searchSource) {
        return getSearchResponse(new SearchRequest(esAliases.getIndex()).types(esAliases.getType()).source(searchSource));
    }

    @Override
    public SearchResponse getSearchResponse(SearchRequest request) {
        SearchResponse response = null;
        try {
            RestHighLevelClient hightClient = esClient.getRestHighLevelClient();
            long l = System.currentTimeMillis();
            response = hightClient.search(request);
            long l1 = System.currentTimeMillis() - l;
            log.info("查询时间：{}", l1);
        } catch (IOException e) {
            log.info("查询数据出错：{}异常：{}", request, e);
        }

        return response;
    }

    @Override
    public Response deleteByQuery(EsAliases esAliases, QueryBuilder query) {
        Response response = null;
        try {
            response = esClient.getRestClient().performRequest("post", "/" + esAliases.getIndex() + "/" + esAliases.getType() + "/_delete_by_query", Collections.emptyMap());
        } catch (IOException e) {
            log.error("根据查询条件删除出错：{}", e.getMessage(), e);
        }
        return response;
    }

    @Override
    public long countByQuery(EsAliases esAliases, QueryBuilder query) {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(query).size(0).explain(false);
        SearchRequest request = new SearchRequest().indices(esAliases.getIndex())
                .types(esAliases.getType()).source(source);
        try {
            SearchResponse response = esClient.getRestHighLevelClient().search(request);
            return response.getHits().getTotalHits();
        } catch (IOException e) {
            log.error("统计总数出错: {}", e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public void updateToProcessor(String index, String type, String id, String data) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(data, XContentType.JSON);
        bulkProcessor.add(updateRequest);
    }

    @Override
    public void upsertToProcessor(String index, String type, String id, String insertData, String updateData) {
        IndexRequest indexRequest = new IndexRequest(index, type, id).source(insertData, XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(updateData, XContentType.JSON).upsert(indexRequest);
        bulkProcessor.add(updateRequest);
    }

    @Override
    public void upsertToProcessor(String index, String type, String id, String data) {
        this.upsertToProcessor(index, type, id, data, data);
    }

    /**
     * 关闭ES客户端
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void closeClient() throws IOException {
        esClient.close();
    }
}
