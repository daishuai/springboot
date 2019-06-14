package com.daishuai.es.config;

import com.daishuai.es.handler.DefaultEsFailureHandler;
import com.daishuai.es.handler.EsFailureHandler;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Daishuai
 * @description BulkProcessor配置
 * @date 2019/4/22 17:05
 */
@Slf4j
@Configuration
public class BulkProcessorConfig {

    @Value("${spring.elasticsearch.default-cluster:default}")
    String defaultDbName;

    @Value("${spring.elasticsearch.bulkProcessor.bulkActions:1000}")
    private int bulkActions;

    @Value("${spring.elasticsearch.bulkProcessor.bulkSize:5}")
    private int bulkSize;

    @Value("${spring.elasticsearch.bulkProcessor.flushInterval:5}")
    private int flushInterval;

    @Value("${spring.elasticsearch.bulkProcessor.concurrentRequests:5}")
    private int concurrentRequests;

    @Autowired
    private BulkProcessor.Listener bulkProcessorListener;

    @Bean
    @ConditionalOnMissingBean
    public BulkProcessor bulkProcessor(EsClientFactory esClientFactory) {
        ThreadPool threadPool = new ThreadPool(Settings.EMPTY);

        final RestHighLevelClient hightClient = esClientFactory.getHightClient(defaultDbName);
        BulkProcessor.Builder builder = new BulkProcessor.Builder(hightClient::bulkAsync, bulkProcessorListener, threadPool);
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

    @Bean
    @ConditionalOnMissingBean
    public EsFailureHandler esFailureHandler() {
        return new DefaultEsFailureHandler();
    }
}
