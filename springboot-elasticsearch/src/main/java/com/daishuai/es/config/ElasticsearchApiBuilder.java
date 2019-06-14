package com.daishuai.es.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.threadpool.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ElasticsearchApiBuilder {

    private static Map<String, RestElasticsearchApi> apiMap = new HashMap<>();
    private static Map<String, BulkProcessor> bulkProcessorMap = new HashMap<>();

    @Autowired
    EsClientFactory esClientFactory;

    @Autowired
    private BulkProcessor.Listener listener;

    @Value("${spring.elasticsearch.bulkProcessor.bulkActions:1000}")
    private int bulkActions;

    @Value("${spring.elasticsearch.bulkProcessor.bulkSize:5}")
    private int bulkSize;

    @Value("${spring.elasticsearch.bulkProcessor.flushInterval:5}")
    private int flushInterval;

    @Value("${spring.elasticsearch.bulkProcessor.concurrentRequests:5}")
    private int concurrentRequests;

    public synchronized RestElasticsearchApi build(String dbName) {
        if (apiMap.containsKey(dbName)) {
            return apiMap.get(dbName);
        } else {
            BulkProcessor bulkProcessor = null;
            if (bulkProcessorMap.containsKey(dbName)) {
                bulkProcessor = bulkProcessorMap.get(dbName);
            } else {
                bulkProcessor = createBulkProcessor(dbName);
                bulkProcessorMap.put(dbName, bulkProcessor);
            }
            apiMap.put(dbName, new RestElasticsearchApi(this.esClientFactory, dbName, bulkProcessor));
            return apiMap.get(dbName);
        }
    }

    /**
     * 新建批处理器
     *
     * @param dbName
     * @return
     */
    private BulkProcessor createBulkProcessor(String dbName) {
        ThreadPool threadPool = new ThreadPool(Settings.EMPTY);

        final RestHighLevelClient hightClient = esClientFactory.getHightClient(dbName);
        BulkProcessor.Builder builder = new BulkProcessor.Builder(hightClient::bulkAsync, listener, threadPool);
        //消息数量到达1000
        builder.setBulkActions(bulkActions)
                //消息大小到大5M
                .setBulkSize(new ByteSizeValue(bulkSize, ByteSizeUnit.MB))
                //时间达到5s
                .setFlushInterval(TimeValue.timeValueSeconds(flushInterval))
                //并发数
                .setConcurrentRequests(concurrentRequests);
        return builder.build();
    }
}
