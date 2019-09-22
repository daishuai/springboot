package com.daishuai.excel.controller;

import com.daishuai.common.entity.ResponseEntity;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.es.enums.EsAliases;
import com.daishuai.excel.constant.CommonCache;
import com.daishuai.excel.service.ExcelService;
import com.daishuai.excel.service.ExportService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 12:10
 */
@RestController
public class ExcelController {
    
    @Autowired
    private ExportService exportService;
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    /**
     * 导出数据
     *
     * @param downloadId
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/export/{downloadId}")
    public ResponseEntity export(@PathVariable(name = "downloadId") String downloadId, HttpServletResponse response) {
    
        BoolQueryBuilder boolQuery = boolQuery();

        exportService.startSearchDb(boolQuery, null, EsAliases.RYXX, "ryxx", downloadId);
        
        return ResponseEntity.success();
    }
    
    /**
     * 下载进度
     *
     * @param downloadId
     * @return
     */
    @GetMapping("/progress/{downloadId}")
    public ResponseEntity getDownloadProcess(@PathVariable(name = "downloadId") String downloadId) {
        Object value = CommonCache.downloadProgress.get(downloadId);
        if (value == null) {
            return ResponseEntity.error("下载任务不存在，downloadId:" + downloadId);
        }
        if (value instanceof Integer) {
            return ResponseEntity.success(value);
        } else {
            CommonCache.downloadProgress.remove(downloadId);
            return ResponseEntity.success(value);
        }
    }
    
    /**
     * 取消下载
     *
     * @param downloadId
     * @return
     */
    @GetMapping("/cancel/{downloadId}")
    public ResponseEntity cancelDownload(@PathVariable(name = "downloadId") String downloadId) {
        if (CommonCache.downloadProgress.containsKey(downloadId)) {
            CommonCache.downloadProgress.remove(downloadId);
            return ResponseEntity.success();
        } else {
            return ResponseEntity.error("下载任务不存在，downloadId:" + downloadId);
        }
    }
    
}
