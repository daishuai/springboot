package com.daishuai.es.service;

import com.daishuai.es.enums.EsAliases;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description ES API
 * @createTime 2022年05月27日 12:24:00
 */
public interface ElasticsearchApi {

    SearchResponse searchData(EsAliases esAliases, SearchSourceBuilder searchSource);

    SearchResponse getSearchResponse(SearchRequest request);

    /**
     * 根据条件删除数据
     *
     * @param esAliases 索引
     * @param query     条件
     * @return 删除结果
     */
    Response deleteByQuery(EsAliases esAliases, QueryBuilder query);

    /**
     * 计算总数
     *
     * @param esAliases 索引
     * @param query     条件
     * @return 总数
     */
    long countByQuery(EsAliases esAliases, QueryBuilder query);

    /**
     * 批量处理
     *
     * @param index 索引
     * @param type  类型
     * @param id    ID
     * @param data 不存在则用此数据插入
     */
    void updateToProcessor(String index, String type, String id, String data);

    /**
     * 批量处理
     *
     * @param index 索引
     * @param type  类型
     * @param id    ID
     * @param insertData 不存在则用此数据插入
     * @param updateData 存在则用此数据更新
     */
    void upsertToProcessor(String index, String type, String id, String insertData, String updateData);

    /**
     * 批量存在则更新，不存在则插入
     *
     * @param index 索引
     * @param type  类型
     * @param id    ID
     * @param data  数据
     */
    void upsertToProcessor(String index, String type, String id, String data);

    /**
     * 关闭ES客户端
     *
     * @throws IOException if an I/O error occurs
     */
    void closeClient() throws IOException;
}
