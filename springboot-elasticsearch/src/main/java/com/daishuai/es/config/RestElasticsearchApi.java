package com.daishuai.es.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author kedacom
 * @date 2019-02-25
 */
@Slf4j
@Component
public class RestElasticsearchApi {
    @Autowired
    private ElasticsearchApiBuilder elasticsearchApiBuilder;

    @Autowired
    private EsClientFactory esClientFactory;

    @Value("${spring.elasticsearch.default-cluster:default}")
    private String defaultDbName;

    @Value("${spring.elasticsearch.datasource.dynamic.enable:false}")
    private boolean dynamic;

    @Autowired
    private BulkProcessor bulkProcessor;

    protected RestElasticsearchApi() {
    }

    protected RestElasticsearchApi(EsClientFactory esClientFactory, String defaultDbName, BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
        this.defaultDbName = defaultDbName;
        this.esClientFactory = esClientFactory;
    }


    @Deprecated()
    public synchronized RestElasticsearchApi build(String dbName) {
        return elasticsearchApiBuilder.build(dbName);
    }


    public Response createIndex(String index, JSONObject metaData) {
        if (existIndex(index)) {
            log.info(defaultDbName + " : " + index + "索引已存在");
            return null;
        }

        HttpEntity entity = new NStringEntity(metaData.toJSONString(), ContentType.APPLICATION_JSON);
        Response response = null;
        try {
            response = esClientFactory.getLowClient(defaultDbName).performRequest("put", "/" + index, Collections.emptyMap(), entity);
        } catch (IOException e) {
            log.error("创建索引出错：{}", e);
        }

        log.info("==={}===", response);
        return response;
    }


    public Boolean deleteIndex(String index) {
        Response response = null;
        try {
            response = esClientFactory.getLowClient(defaultDbName).performRequest("delete", "/" + index);
        } catch (ResponseException e) {
            response = e.getResponse();
        } catch (IOException e) {
            log.error("删除索引出错：{}", e);
            return false;
        }

        return response.getStatusLine().getStatusCode() == 200;
    }


    public Boolean existIndex(String index) {
        Response response = null;
        try {
            response = esClientFactory.getLowClient(defaultDbName).performRequest("head", "/" + index, Collections.emptyMap());
        } catch (ResponseException e) {
            response = e.getResponse();
        } catch (IOException e) {
            log.error("检测索引出错：{}", e);
            return false;
        }
        return response.getStatusLine().getStatusCode() == 200;
    }


    public SearchResponse searchScroll(SearchScrollRequest request) {
        SearchResponse response = null;
        try {
            response = esClientFactory.getHightClient(defaultDbName).searchScroll(request);
        } catch (IOException e) {
            log.info("插入数据出错：{}异常：{}", request, e);
        }
        return response;
    }

    public IndexResponse getIndexResponse(IndexRequest request) {
        IndexResponse response = null;

        try {
            response = esClientFactory.getHightClient(defaultDbName).index(request);
        } catch (IOException e) {
            log.info("插入数据出错：{}异常：{}", request, e);
        }
        return response;
    }

    public DeleteResponse getDeleteResponse(DeleteRequest request) {
        DeleteResponse response = null;
        try {
            response = esClientFactory.getHightClient(defaultDbName).delete(request);
        } catch (IOException e) {
            log.info("删除数据出错：{}异常：{}", request, e);
        }
        return response;
    }

    public UpdateResponse getUpdateResponse(UpdateRequest request) {
        UpdateResponse response = null;
        try {
            response = esClientFactory.getHightClient(defaultDbName).update(request);
        } catch (IOException e) {
            log.info("更新数据出错：{}异常：{}", request, e);
        }
        return response;
    }

    public BulkResponse getBulkResponse(BulkRequest request) {
        BulkResponse response = null;
        try {
            response = esClientFactory.getHightClient(defaultDbName).bulk(request);
        } catch (IOException e) {
            log.info("批量插入数据出错：{}异常：{}", request, e);
            return response;
        }
        if (response.hasFailures()) {
            log.info("批量插入文档失败：{}", response.buildFailureMessage());
        } else {
            log.info("批量插入文档成功：{}");
        }
        return response;
    }

    public GetResponse getGetResponse(GetRequest request) {
        GetResponse response = null;
        String clusterName = defaultDbName;
        if (dynamic) {
            String index = request.index();
            String[] strs = StringUtils.split(index, ":");
            if (ArrayUtils.isNotEmpty(strs) && strs.length == 2) {
                clusterName = strs[0];
                request.index(strs[1]);
            }
        }
        try {
            response = esClientFactory.getHightClient(clusterName).get(request);
        } catch (IOException e) {
            log.info("获取数据出错：{}异常：{}", request, e);
        }
        return response;
    }

    public SearchResponse getSearchResponse(SearchRequest request) {
        SearchResponse response = null;
        String clusterName = defaultDbName;
        if (dynamic) {
            String[] indices = request.indices();
            if (indices.length == 1) {
                String[] strs = StringUtils.split(indices[0], ":");
                if (ArrayUtils.isNotEmpty(strs) && strs.length == 2) {
                    clusterName = strs[0];
                    request.indices(strs[1]);
                }
            }
        }
        try {
            RestHighLevelClient hightClient = esClientFactory.getHightClient(clusterName);
            long l = System.currentTimeMillis();
            response = hightClient.search(request);
            long l1 = System.currentTimeMillis() - l;
            log.info("查询时间：{}", l1);
        } catch (IOException e) {
            log.info("查询数据出错：{}异常：{}", request, e);
        }

        return response;
    }

    public IndexResponse setDataImmediate(String index, String type, Map source) {
        return getIndexResponse(new IndexRequest(index, type).source(source).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE));
    }

    public IndexResponse setDataWithIdImmediate(String index, String type, String id, Map source) {
        return getIndexResponse(new IndexRequest(index, type, id).source(source).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE));
    }

    public IndexResponse setData(String index, String type, Map source) {
        return getIndexResponse(new IndexRequest(index, type).source(source));
    }

    public IndexResponse setDataWithId(String index, String type, String id, Map source) {
        return getIndexResponse(new IndexRequest(index, type, id).source(source));
    }

    public IndexResponse setDataWithParent(String index, String type, Map source, String parent) {
        return getIndexResponse(new IndexRequest(index, type).source(source).parent(parent));
    }

    public IndexResponse setDataWithParentAndId(String index, String type, String id, Map source, String parent) {
        return getIndexResponse(new IndexRequest(index, type, id).source(source).parent(parent));
    }

    public BulkResponse setListData(String index, String type, List beanList) {

        return setListDataWithId(index, type, beanList, "id");

    }

    public BulkResponse setListDataWithId(String index, String type, List beanList, String idFieldName) {
        return setListDataWithParentAndId(index, type, beanList, idFieldName, null);
    }

    public BulkResponse setListDataWithParent(String index, String type, List beanList, String parentFieldName) {
        return setListDataWithParentAndId(index, type, beanList, null, parentFieldName);
    }

    public BulkResponse setListDataWithParentAndId(String index, String type, List<Object> beanList, String idFieldName, String parentFieldName) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Object o : beanList) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSON(o).toString());
            IndexRequest request = new IndexRequest(index, type).source(jsonObject.getInnerMap());
            if (idFieldName != null && jsonObject.containsKey(idFieldName)) {
                request.id(jsonObject.getString(idFieldName));
            }

            if (parentFieldName != null && jsonObject.containsKey(parentFieldName)) {
                request.parent(jsonObject.getString(parentFieldName));
            }

            bulkRequest.add(request);
        }

        return getBulkResponse(bulkRequest);
    }

    public UpdateResponse updateById(String index, String type, String id, Map source) {
        return getUpdateResponse(new UpdateRequest(index, type, id).doc(source).docAsUpsert(true));
    }

    public UpdateResponse updateByIdImmediate(String index, String type, String id, Map source) {
        return getUpdateResponse(new UpdateRequest(index, type, id).doc(source).docAsUpsert(true).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE));
    }

    public GetResponse getDataById(String index, String type, String id) {
        return getGetResponse(new GetRequest(index, type, id));
    }

    public SearchResponse getDateByIds(String index, String type, String[] ids) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(QueryBuilders.idsQuery().addIds(ids))
                .timeout(new TimeValue(60, TimeUnit.SECONDS));
        return getSearchResponse(new SearchRequest(index).types(type).source(sourceBuilder));
    }

    public SearchResponse searchData(String index, String type, SearchSourceBuilder searchSource) {
        return getSearchResponse(new SearchRequest(index).types(type).source(searchSource));
    }

    public SearchSourceBuilder searchSourceBuilder() {
        return new SearchSourceBuilder();
    }

    public SearchSourceBuilder searchSourceBuilder(int pageNo, int pageSize, QueryBuilder query) {
        return searchSourceBuilder(pageNo, pageSize, query, null, null, null, null, null);
    }

    public SearchSourceBuilder searchSourceBuilder(int pageNo, int pageSize, QueryBuilder query, QueryBuilder filter, SortBuilder[] sorts, AggregationBuilder[] aggs, HighlightBuilder highlightBuilder, String[] include) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(pageNo * pageSize)
                .size(pageSize)
                .timeout(new TimeValue(10, TimeUnit.SECONDS))
                .fetchSource(include, null);
        if (query != null) {
            sourceBuilder.query(query);
        } else {
            sourceBuilder.query(QueryBuilders.matchAllQuery());
        }
        if (sorts != null && sorts.length > 0) {
            for (SortBuilder sort : sorts) {
                sourceBuilder.sort(sort);
            }
        }

        if (filter != null) {
            sourceBuilder.postFilter(filter);
        }

        if (highlightBuilder != null) {
            sourceBuilder.highlighter(highlightBuilder);
        }

        if (aggs != null && aggs.length > 0) {
            for (AggregationBuilder agg : aggs) {
                sourceBuilder.aggregation(agg);
            }
        }

        return sourceBuilder;
    }

    public DeleteResponse deleteById(String index, String type, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        deleteRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        return getDeleteResponse(deleteRequest);
    }

    public BulkResponse deleteByIds(String index, String type, String[] ids) {
        BulkRequest bulkRequest = new BulkRequest();
        for (String id : ids) {
            DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
            bulkRequest.add(deleteRequest);
        }

        bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        return getBulkResponse(bulkRequest);
    }

    public Response deleteByQuery(String index, String type, QueryBuilder query) {

        HttpEntity entity = new NStringEntity(new SearchSourceBuilder().query(query).toString(), ContentType.APPLICATION_JSON);
        Response response = null;
        try {
            response = esClientFactory.getLowClient(defaultDbName).performRequest("post", "/" + index + "/" + type + "/_delete_by_query", Collections.emptyMap(), entity);
        } catch (IOException e) {
            log.error("根据查询条件删除出错：{}", e);
        }
        return response;
    }

    public List<JSONObject> hitsConvert(SearchResponse response) {
        return hitsConvert(response, JSONObject.class);
    }

    public <T> List<T> hitsConvert(SearchResponse response, Class<T> f) {
        SearchHit[] data = response.getHits().getHits();
        List<T> datas = new ArrayList<>();
        //遍历取出数据
        for (SearchHit datum : data) {
            Map<String, Object> source = datum.getSourceAsMap();
            source.putAll(datum.getHighlightFields());
            Map<String, HighlightField> highlightFields = datum.getHighlightFields();
            //将高亮片段组装到结果中去
            for (String fieldName : highlightFields.keySet()) {
                HighlightField nameField = highlightFields.get(fieldName);
                if (nameField != null) {
                    Text[] fragments = nameField.fragments();
                    StringBuilder nameTmp = new StringBuilder();
                    for (Text text : fragments) {
                        nameTmp.append(text);
                    }
                    source.put(fieldName, nameTmp.toString());
                }
            }

            JSONObject jsonObject = new JSONObject(source);
            jsonObject.put("id", datum.getId());
            T t = jsonObject.toJavaObject(f);
            datas.add(t);
        }

        return datas;
    }

    public List<Map<String, Object>> pageDataByScroll(String index, String type, QueryBuilder query, int pageNo, int pageSize, SortBuilder sort) {

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(query)
                .size(5000)
                .explain(false);
        if (sort != null) {
            source.sort(sort);
        }
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));

        SearchRequest request = new SearchRequest()
                .indices(index)
                .types(type)
                .source(source)
                .scroll(scroll);

        SearchResponse searchResponse = this.getSearchResponse(request);
        long total = searchResponse.getHits().getTotalHits();
        long pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        pageNo = Math.toIntExact(pageNo > pages ? pages : pageNo);
        SearchHit[] hits = searchResponse.getHits().getHits();
        String scrollId = searchResponse.getScrollId();

        int from = pageSize * pageNo + 1;
        int to = (pageNo + 1) * pageSize;
        int sum = 0;

        List<Map<String, Object>> result = new ArrayList();
        while (ArrayUtils.isNotEmpty(hits)) {
            int sumfrom = sum + 1;
            sum = sum + hits.length;
            if (from >= sumfrom && to <= sum) {
                for (int i = from - sumfrom; i <= to - sumfrom; i++) {
                    result.add(hits[i].getSource());
                }
                break;
            }
            if (from < sumfrom && to >= sumfrom && to < sum) {
                for (int i = 0; i <= to - sumfrom; i++) {
                    result.add(hits[i].getSource());
                }
                break;
            }

            if (from > sumfrom && from <= sum && to > sum) {
                for (int i = from - sumfrom; i <= sum - sumfrom; i++) {
                    result.add(hits[i].getSource());
                }
            }
            if (from <= sumfrom && to >= sum) {
                for (int i = 0; i <= sum - sumfrom; i++) {
                    result.add(hits[i].getSource());
                }
            }

            if (result.size() == pageSize) {
                break;
            }

            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            SearchResponse response = this.searchScroll(scrollRequest);
            scrollId = response.getScrollId();
            hits = response.getHits().getHits();
        }
        this.clearScroll(scrollId);
        return result;
    }

    public List<Map<String, Object>> searchDataByScroll(String index, String type, QueryBuilder query, SortBuilder sort) {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(query)
                .size(5000)
                .explain(false);
        if (sort != null) {
            source.sort(sort);
        }
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));

        SearchRequest request = new SearchRequest()
                .indices(index)
                .types(type)
                .source(source)
                .scroll(scroll);

        SearchResponse searchResponse = this.getSearchResponse(request);
        SearchHit[] hits = searchResponse.getHits().getHits();
        String scrollId = searchResponse.getScrollId();

        List<Map<String, Object>> result = new ArrayList();
        while (ArrayUtils.isNotEmpty(hits)) {
            Arrays.stream(hits).forEach(hit -> result.add(hit.getSource()));
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            SearchResponse response = this.searchScroll(scrollRequest);
            scrollId = response.getScrollId();
            hits = response.getHits().getHits();
        }
        this.clearScroll(scrollId);
        return result;
    }

    public void clearScroll(String scrollId) {
        ClearScrollRequest request = new ClearScrollRequest();
        request.addScrollId(scrollId);
        try {
            esClientFactory.getHightClient(defaultDbName).clearScroll(request);
        } catch (IOException e) {
            log.error("清理scroll出错：{}", e.getMessage(), e);
        }
    }

    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param id
     * @param data
     */
    public void updateToProcessor(String index, String type, String id, String data) {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(data, XContentType.JSON);
        bulkProcessor.add(updateRequest);
    }

    /**
     * 批量处理
     *
     * @param index
     * @param type
     * @param id
     * @param insertData 不存在则用此数据插入
     * @param updateData 存在则用此数据更新
     */
    public void upsertToProcessor(String index, String type, String id, String insertData, String updateData) {
        IndexRequest indexRequest = new IndexRequest(index, type, id).source(insertData, XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(updateData, XContentType.JSON).upsert(indexRequest);
        bulkProcessor.add(updateRequest);
    }

    /**
     * 批量存在则更新，不存在则插入
     *
     * @param index
     * @param type
     * @param id
     * @param data
     */
    public void upsertToProcessor(String index, String type, String id, String data) {
        this.upsertToProcessor(index, type, id, data, data);
    }

    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param datas key为id，value为要新增的数据
     */
    public void batchUpdate(String index, String type, Map<String, Object> datas) {
        BulkRequest bulkRequest = new BulkRequest();
        datas.entrySet().forEach(data -> {
            UpdateRequest updateRequest = new UpdateRequest(index, type, data.getKey())
                    .doc(JSON.parseObject(JSON.toJSONString(data.getValue())))
                    .docAsUpsert(true);
            bulkRequest.add(updateRequest);

        });
        this.getBulkResponse(bulkRequest);

    }

    /**
     * 批量新增
     *
     * @param index
     * @param type
     * @param datas key为id，value为要新增的数据
     */
    public void batchInsert(String index, String type, Map<String, Object> datas) {
        BulkRequest bulkRequest = new BulkRequest();
        datas.entrySet().forEach(data -> {
            IndexRequest indexRequest = new IndexRequest(index, type, data.getKey())
                    .source(JSON.parseObject(JSON.toJSONString(data.getValue())).getInnerMap());
            bulkRequest.add(indexRequest);

        });
        this.getBulkResponse(bulkRequest);

    }

}
