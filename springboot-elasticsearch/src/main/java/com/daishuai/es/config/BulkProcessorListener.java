package com.daishuai.es.config;

import com.daishuai.es.handler.EsFailureHandler;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Daishuai
 * @description ES批处理监听器
 * @date 2019/5/28 10:49
 */
@Slf4j
@Component
public class BulkProcessorListener implements BulkProcessor.Listener {

    @Autowired
    private EsFailureHandler failureHandler;

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
        log.info("---尝试插入{}条数据---", request.numberOfActions());
    }

    @Override
    public void afterBulk(long executionId,
                          BulkRequest request, BulkResponse response) {
        long failed = 0;
        long success = request.numberOfActions();
        List<DocWriteRequest> requests = request.requests();
        if (response.hasFailures()) {
            BulkItemResponse[] items = response.getItems();
            if (ArrayUtils.isNotEmpty(items)) {
                for (BulkItemResponse item : items) {
                    if (item.isFailed()) {
                        failureHandler.handleFailure(item.getIndex(),
                                item.getType(),
                                item.getId(),
                                item.getOpType().name(),
                                getDoc(requests.get(item.getItemId())),
                                item.getFailure().getCause());
                        failed++;
                    }
                }
            }
        }
        success = success - failed;
        log.info("---插入{}条数据成功---", success);
        log.info("---插入{}条数据失败---", failed);
    }

    @Override
    public void afterBulk(long executionId,
                          BulkRequest request, Throwable failure) {
        List<DocWriteRequest> requests = request.requests();
        if (CollectionUtils.isNotEmpty(requests)) {
            for (DocWriteRequest docWriteRequest : requests) {
                failureHandler.handleFailure(docWriteRequest.index(),
                        docWriteRequest.type(),
                        docWriteRequest.id(),
                        docWriteRequest.opType().name(),
                        getDoc(docWriteRequest),
                        failure);
            }
        }
        log.error("[es错误]---尝试插入数据失败---", failure);
    }

    private Map<String, Object> getDoc(DocWriteRequest docWriteRequest) {
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
