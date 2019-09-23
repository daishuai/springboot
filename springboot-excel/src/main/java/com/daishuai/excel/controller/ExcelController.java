package com.daishuai.excel.controller;

import com.daishuai.common.entity.ResponseEntity;
import com.daishuai.es.enums.EsAliases;
import com.daishuai.excel.constant.CommonCache;
import com.daishuai.excel.service.ExcelService;
import com.daishuai.excel.service.ExportService;
import com.daishuai.jpa.repository.ExportModelDao;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

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
    private ExcelService excelService;
    
    @Autowired
    private ExportModelDao exportModelDao;
    
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
        FieldSortBuilder rksj = SortBuilders.fieldSort("RKSJ").order(SortOrder.DESC);
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
    
    @GetMapping("/test")
    public ResponseEntity testSheet(HttpServletResponse response) throws Exception {
        excelService.commonExport("zqfx", null, "灾情分析", response);
        return ResponseEntity.success();
    }
    
}
