package com.daishuai.es.config;


import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author wangkaiqi
 * @date Created in 2019/4/12 13:51
 */
@Component
@Log4j
public class EsClientFactory {
    private static Map<String, ElasticsearchRestClient> restClients = new Hashtable<>();
    private static Map<String, ElasticsearchNodeInfo> restClientInfo = new Hashtable<>();
    private Object obj = new Object();
    @Autowired
    private EsConfig esConfig;

    public EsClientFactory() {

    }

    public Boolean addDataSource(ElasticsearchNodeInfo elasticsearchNodeInfo) {
        if (elasticsearchNodeInfo != null && elasticsearchNodeInfo.getRestNodes() != null && elasticsearchNodeInfo.getClusterName() != null) {
            if (esConfig.getClusters().containsKey(elasticsearchNodeInfo.getClusterName())) {
                log.info("数据源已存在：" + esConfig.getClusters().get(elasticsearchNodeInfo.getClusterName()).toString());
                return false;
            } else {
                restClientInfo.put(elasticsearchNodeInfo.getClusterName(), elasticsearchNodeInfo);
                return true;
            }
        } else {
            return false;
        }
    }

    public void init(String name) {
        if (!restClients.containsKey(name)) {
            if (esConfig.getClusters().containsKey(name)) {
                esConfig.getClusters().get(name).setClusterName(name);
                synchronized (obj) {
                    this.createClient(this.esConfig.getClusters().get(name));
                }
            } else if (restClientInfo.containsKey(name)) {
                restClientInfo.get(name).setClusterName(name);
                synchronized (obj) {
                    this.createClient(restClientInfo.get(name));
                }
            } else {
                throw new NullPointerException("要连接的es集群名称不存在");
            }
        }
    }


    public EsConfig getEsConfig() {
        return esConfig;
    }

    public RestHighLevelClient getHightClient() {
        return getHightClient(esConfig.getClusters().get(0).getClusterName());
    }

    public RestClient getLowClient() {
        return getLowClient(esConfig.getClusters().get(0).getClusterName());
    }

    public RestHighLevelClient getHightClient(String name) {
        init(name);
        return restClients.getOrDefault(name, new ElasticsearchRestClient()).getS_HighClient();
    }

    public RestClient getLowClient(String name) {
        init(name);
        return restClients.getOrDefault(name, new ElasticsearchRestClient()).getS_LowClient();
    }

    private synchronized void createClient(ElasticsearchNodeInfo elasticsearchNode) {

        RestClient s_LowClient;
        RestHighLevelClient s_HighClient;

        log.info("Creating client：" + elasticsearchNode.toString());
        try {
            List<HttpHost> hostArray = new ArrayList<>();
            String[] nodes = elasticsearchNode.getRestNodes().split(",");
            for (String item : nodes) {
                String[] host = item.split(":");
                if (host.length == 2 && !host[0].equals("") && !host[1].equals("")) {
                    hostArray.add(new HttpHost(host[0], Integer.parseInt(host[1]), "http"));
                }
            }

            //加密对象
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            if (elasticsearchNode.getUsername() != null && elasticsearchNode.getPassword() != null) {
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchNode.getUsername(), elasticsearchNode.getPassword()));
            }

            s_LowClient = RestClient.builder(hostArray.toArray(new HttpHost[0])).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    //设置加密
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                            //设置线程数
                            .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(elasticsearchNode.getThreadNum()).build());
                }
            }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                    return builder.setConnectTimeout(elasticsearchNode.getConnectTimeout())
                            .setSocketTimeout(elasticsearchNode.getConnectTimeout() * 2)
                            .setConnectionRequestTimeout(elasticsearchNode.getConnectTimeout());
                }
            }).setMaxRetryTimeoutMillis(5 * 60 * 1000).build();

            s_HighClient = new RestHighLevelClient(s_LowClient);
            Sniffer sniffer = Sniffer.builder(s_LowClient)
                    .setSniffIntervalMillis(60000).build();
            ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient();
            elasticsearchRestClient.setS_LowClient(s_LowClient);
            elasticsearchRestClient.setS_HighClient(s_HighClient);
            restClients.put(elasticsearchNode.getClusterName(), elasticsearchRestClient);
        } catch (Exception var3) {
            log.error("创建es restClient [" + elasticsearchNode.getClusterName() + "]错误：{}", var3);
        }

    }

    @Data
    class ElasticsearchRestClient {
        private RestClient s_LowClient;
        private RestHighLevelClient s_HighClient;
    }
}

