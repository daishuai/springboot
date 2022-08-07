package com.daishuai.es.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;

import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description 批处理器监听器
 * @createTime 2022-08-07 21:50:48
 */
@Slf4j
public class BulkProcessorListener implements BulkProcessor.Listener {
    @Override
    public void beforeBulk(long executionId, BulkRequest request) {

    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        long failed = 0;
        long success = request.numberOfActions();
        BulkItemResponse[] items = response.getItems();
        List<DocWriteRequest<?>> requests = request.requests();
        if (ArrayUtils.isEmpty(items)) {
            return;
        }
        for (BulkItemResponse item : items) {
            if (!item.isFailed()) {
                continue;
            }
            log.error("index:{}, type:{}, id:{}, opType:{}, doc:{}, failureMessage:{}",
                    item.getIndex(),
                    item.getType(),
                    item.getId(),
                    item.getOpType().name(),
                    getDoc(requests.get(item.getItemId())),
                    item.getFailure().getCause());
            failed++;
        }
        success = success - failed;
        log.info("---插入{}条数据成功---", success);
        log.info("---插入{}条数据失败---", failed);
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        log.error("[es错误]---尝试插入数据失败---", failure);
        List<DocWriteRequest<?>> requests = request.requests();
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }
        for (DocWriteRequest<?> docWriteRequest : requests) {
            log.error("index:{}, type:{}, id:{}, opType:{}, doc:{}, failureMessage:{}",
                    docWriteRequest.index(),
                    docWriteRequest.type(),
                    docWriteRequest.id(),
                    docWriteRequest.opType().name(),
                    getDoc(docWriteRequest),
                    failure);
        }
    }
    private Map<String, Object> getDoc(DocWriteRequest<?> docWriteRequest) {
        Map<String, Object> doc = null;
        if (docWriteRequest instanceof UpdateRequest) {
            UpdateRequest updateRequest = (UpdateRequest) docWriteRequest;
            doc = updateRequest.doc().sourceAsMap();
        } else if (docWriteRequest instanceof IndexRequest) {
            IndexRequest indexRequest = (IndexRequest) docWriteRequest;
            doc = indexRequest.sourceAsMap();
        }
        return doc;
    }
}
