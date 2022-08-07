package com.daishuai.es.factory;

import com.daishuai.es.config.BulkProcessorListener;
import com.daishuai.es.config.ElasticsearchNode;
import com.daishuai.es.service.ElasticsearchApi;
import com.daishuai.es.service.RestElasticsearchApi;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description ES API工厂
 * @createTime 2022年05月27日 13:03:00
 */
@Slf4j
public class RestEsApiFactory {

    private static final Map<String, ElasticsearchApi> esApiMap = new ConcurrentHashMap<>();

    public static ElasticsearchApi getEsApi(String clusterName) {
        return esApiMap.get(clusterName);
    }

    public static ElasticsearchApi getRandomEsApi() {
        return esApiMap.values().stream().findAny().orElse(null);
    }

    public static ElasticsearchApi buildEsApi(ElasticsearchNode node) {
        String clusterName = node.getClusterName();
        ElasticsearchApi elasticsearchApi = esApiMap.get(clusterName);
        if (elasticsearchApi != null) {
            log.info("ES API已存在: {}", clusterName);
            return elasticsearchApi;
        }
        ElasticsearchRestClient esClient = EsClientFactory.getEsClient(node);
        if (esClient == null) {
            log.warn("ES集群[{}]客户端不存在.", clusterName);
            return null;
        }
        elasticsearchApi = new RestElasticsearchApi(esClient, getBulkProcessor(esClient.getRestHighLevelClient(), node));
        esApiMap.put(clusterName, elasticsearchApi);
        return elasticsearchApi;
    }

    private static BulkProcessor getBulkProcessor(RestHighLevelClient highLevelClient, ElasticsearchNode node) {
        BulkProcessor.Builder builder = BulkProcessor.builder(((bulkRequest, bulkResponseActionListener) -> highLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkResponseActionListener)), new BulkProcessorListener());
        //消息数量到达1000
        builder.setBulkActions(node.getBulkActions())
                .setBulkSize(new ByteSizeValue(node.getBulkSize(), ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(node.getFlushInterval()))
                .setConcurrentRequests(node.getConcurrentRequests());
        return builder.build();
    }
}
