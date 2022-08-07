package com.daishuai.es.factory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description ES客户端
 * @createTime 2022年05月27日 11:15:00
 */
@Data
@Slf4j
public class ElasticsearchRestClient implements Closeable {

    private RestClient restClient;
    private RestHighLevelClient restHighLevelClient;


    @Override
    public void close() throws IOException {
        if (restClient != null) {
            try {
                restClient.close();
            } catch (IOException e) {
                log.error("关闭ES客户端异常: {}", e.getMessage(), e);
            }
        }
    }
}
