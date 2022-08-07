package com.daishuai.es.factory;

import com.alibaba.fastjson.JSON;
import com.daishuai.es.config.ElasticsearchNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description ES客户端工厂
 * @createTime 2022年05月27日 09:29:00
 */
@Slf4j
public class EsClientFactory {


    private static final String KEY_PATH_PREFIX = "pathPrefix";
    private static final String KEY_HTTP_HOST = "httpHost";

    /**
     * ES客户端缓存
     */
    private static final Map<String, ElasticsearchRestClient> esClientMap = new ConcurrentHashMap<>();


    public ElasticsearchRestClient getEsClient(String clusterName) {
        return esClientMap.get(clusterName);
    }

    public static ElasticsearchRestClient getEsClient(ElasticsearchNode node) {
        String clusterName = node.getClusterName();
        ElasticsearchRestClient client = esClientMap.get(clusterName);
        if (client == null) {
            try {
                client = buildEsClient(node);
            } catch (Exception e) {
                log.error("创建ES集群 [{}] 客户端出错: {}", clusterName, e.getMessage(), e);
            }
        }
        return client;
    }

    private static ElasticsearchRestClient buildEsClient(ElasticsearchNode node) {
        log.info("Creating client：{}", JSON.toJSONString(node));
        String pathPrefix = "";
        List<HttpHost> hostArray = new ArrayList<>();
        String[] nodes = node.getRestNodes().split(",");
        for (String item : nodes) {
            Map<String, Object> resultMap = createHttpHost(item);
            pathPrefix = (String) resultMap.get(KEY_PATH_PREFIX);
            hostArray.add((HttpHost) resultMap.get(KEY_HTTP_HOST));
        }
        //加密对象
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        String username = node.getUsername();
        String password = node.getPassword();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }
        RestClientBuilder restClientBuilder = RestClient.builder(hostArray.toArray(new HttpHost[0]));
        if (!pathPrefix.equals("") && !pathPrefix.equals("/")) {
            restClientBuilder.setPathPrefix(pathPrefix);
        }
        RestClient restLowClient = restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            //设置加密
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                    //设置线程数
                    .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(node.getThreadNum()).build());
        }).setRequestConfigCallback(builder -> builder.setConnectTimeout(node.getConnectTimeout())
                .setSocketTimeout(node.getConnectTimeout() * 2)
                .setConnectionRequestTimeout(node.getConnectTimeout())).build();
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient();
        elasticsearchRestClient.setRestClient(restLowClient);
        elasticsearchRestClient.setRestHighLevelClient(restHighLevelClient);
        esClientMap.put(node.getClusterName(), elasticsearchRestClient);
        return elasticsearchRestClient;
    }

    /**
     * 构建HttpHost对象，并判断域名是否有相对path
     *
     * @param s
     * @return
     */
    private static Map<String, Object> createHttpHost(final String s) {
        String text = s;
        String scheme = null;
        final int schemeIdx = text.indexOf("://");
        if (schemeIdx > 0) {
            scheme = text.substring(0, schemeIdx);
            text = text.substring(schemeIdx + 3);
        }

        String pathPrefix = "";
        int port = -1;
        final int portIdx = text.lastIndexOf(":");
        if (portIdx > 0) {
            try {
                port = Integer.parseInt(text.substring(portIdx + 1));
            } catch (final NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid HTTP host: " + text);
            }
            text = text.substring(0, portIdx);
        } else {
            final int slashIdx = text.indexOf("/");
            if (slashIdx > 0) {
                pathPrefix = text.substring(slashIdx + 1);
                text = text.substring(0, slashIdx);
            }
        }

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put(KEY_PATH_PREFIX, pathPrefix);
        resultMap.put(KEY_HTTP_HOST, new HttpHost(text, port, scheme));

        return resultMap;
    }
}
