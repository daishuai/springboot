package com.daishuai.excel.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daishuai.es.config.EsClientFactory;
import com.daishuai.es.config.RestElasticsearchApi;
import com.daishuai.es.enums.EsAliases;
import com.daishuai.excel.constant.CommonCache;
import com.daishuai.excel.filter.DefaultExportDataFilter;
import com.daishuai.excel.filter.ExportDataFilter;
import com.daishuai.jpa.entity.ExportModelEntity;
import com.daishuai.jpa.repository.ExportModelDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 14:01
 */
@Slf4j
@Service
public class ExportService {
    
    @Autowired
    private RestElasticsearchApi restElasticsearchApi;
    
    @Autowired
    private EsClientFactory esClientFactory;
    
    @Autowired
    private ExcelService excelService;
    
    @Autowired
    private ExportModelDao exportModelDao;
    
    public void startSearchDb(QueryBuilder query, SortBuilder sortBuilder, EsAliases esAliases, String sheetName, String downloadId) {
        this.startSearchDb(query, sortBuilder, esAliases, sheetName, downloadId, new DefaultExportDataFilter());
    }
    
    public void startSearchDb(QueryBuilder query, SortBuilder sortBuilder, EsAliases esAliases, String sheetName, String downloadId, ExportDataFilter exportDataFilter) {
        
        String dicPath = this.getClass().getResource("").getPath().replace("/WEB-INF", "").replace("/classes", "") + "temp/";
        String fileName = System.currentTimeMillis() + ".xlsx";
        
        ExportModelEntity exportModel = exportModelDao.findBySheetName(sheetName);
        
        String columnName = exportModel.getColumnName();
        String columnField = exportModel.getColumnField();
        List<String> names = JSON.parseArray(columnName, String.class);
        List<String> fields = JSON.parseArray(columnField, String.class);
        
        int totalNums = getTotalNum(query, esAliases);
        int max = 1;
        LinkedBlockingQueue<SearchHit[]> queue = new LinkedBlockingQueue<>();
        SXSSFWorkbook workbook = new SXSSFWorkbook(3000);
        Consumer consumer = new Consumer(workbook, queue, names, fields, sheetName, totalNums, downloadId, exportDataFilter);
        CommonCache.downloadProgress.put(downloadId, 0);
        if (totalNums > 2000000) {
            max = getMax(getShards(esAliases));
            log.debug("=================start multiply export==================");
        }
        log.debug("=================thread number : " + max + "=====================");
        
        ExecutorService pool = Executors.newFixedThreadPool(max);
        for (int i = 0; i < max; i++) {
            pool.execute(new Producer(query, esAliases, queue, i, max, sortBuilder, downloadId));
        }
        pool.shutdown();
        consumer.start();
        while (true) {
            //-1表示取消下载
            if (isNotExistOrCancel(downloadId)) {
                consumer.flag = false;
                CommonCache.downloadProgress.remove(downloadId);
                log.debug("=======================stop export=========================");
                break;
            }
            if (pool.isTerminated()) {
                consumer.flag = false;
            }
            if (!consumer.isAlive() && !isNotExistOrCancel(downloadId)) {
                log.debug("=======================start to write=========================");
                String downLoadPath = writeFiles(workbook, dicPath, fileName);
                log.debug("=======================download url:" + downLoadPath + "=========================");
                CommonCache.downloadProgress.put(downloadId, downLoadPath);
                break;
            }
        }
    }
    
    private String writeFiles(SXSSFWorkbook wb, String dicPath, String fileName) {
        File file = new File(dicPath);
        if (!file.exists()) {
            log.debug("===================didn't have the dir，start to make dir！==============");
            file.mkdirs();
            log.debug("======================make dir success=========================");
        }
        log.debug("========================start to write=======================");
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(dicPath + fileName);
            wb.write(fout);
            fout.close();
            wb.close();
            return "temp/" + fileName;
        } catch (Exception e) {
            log.error("生成文件失败：{}", e.getMessage(), e);
        }
        return "failed";
    }
    
    
    /**
     * 查询索引分片数
     *
     * @param esAliases
     * @return
     */
    private int getShards(EsAliases esAliases) {
        int shards = 1;
        try {
            Response response = esClientFactory.getLowClient().performRequest("GET", esAliases.getIndex() + "/_settings");
            JSONObject responseJson = JSON.parseObject(EntityUtils.toString(response.getEntity()));
            JSONObject indics = JSON.parseObject(responseJson.get(esAliases.getIndex()).toString());
            JSONObject settings = JSON.parseObject(indics.get("settings").toString());
            JSONObject index = JSON.parseObject(settings.get("index").toString());
            shards = Integer.parseInt(index.get("number_of_shards").toString());
        } catch (IOException e) {
            log.error("查询shards出错：{}", e.getMessage(), e);
        }
        return shards;
    }
    
    private int getMax(int a) {
        int result = 1;
        if (a < 6) {
            return a;
        }
        for (int i = 1; i < a; i++) {
            if (a % i == 0) {
                result = i;
            }
        }
        return getMax(result);
    }
    
    /**
     * 查询总数
     *
     * @param query
     * @param esAliases
     * @return
     */
    private int getTotalNum(QueryBuilder query, EsAliases esAliases) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(0)
                .query(query)
                .explain(false);
        SearchRequest searchRequest = new SearchRequest()
                .indices(esAliases.getIndex())
                .types(esAliases.getType())
                .source(searchSourceBuilder);
        SearchResponse searchResponse = restElasticsearchApi.getSearchResponse(searchRequest);
        return (int) searchResponse.getHits().getTotalHits();
    }
    
    /**
     * 判断下载任务是否存在或被取消
     *
     * @param downloadId
     * @return true表示任务不存在或任务被取消
     */
    private boolean isNotExistOrCancel(String downloadId) {
        Object value = CommonCache.downloadProgress.get(downloadId);
        if (value == null) {
            //任务不存在
            return true;
        }
        if (value instanceof Integer) {
            return (Integer) value == -1;
        }
        return false;
    }
    
    /**
     * 生产者
     */
    class Producer extends Thread {
        private QueryBuilder query;
        private SortBuilder sortBuilder;
        private EsAliases esAliases;
        private LinkedBlockingQueue<SearchHit[]> queue;
        private int max;
        private int i;
        private String downloadId;
        
        public Producer(QueryBuilder query, EsAliases esAliases, LinkedBlockingQueue<SearchHit[]> queue, int i, int max, SortBuilder sortBuilder, String downloadId) {
            this.query = query;
            this.esAliases = esAliases;
            this.sortBuilder = sortBuilder;
            this.queue = queue;
            this.max = max;
            this.i = i;
            this.downloadId = downloadId;
        }
        
        @Override
        public void run() {
            try {
                log.debug("====================thread:" + i + "start=======================");
                String scrollId;
                Long nums = 0L;
                SearchHit[] searchHits;
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                        .size(2000)
                        .query(query)
                        .explain(false);
                if (sortBuilder != null && max == 1) {
                    searchSourceBuilder.sort(sortBuilder);
                }
                
                if (max != 1) {
                    searchSourceBuilder.slice(new SliceBuilder(i, max));
                }
                
                SearchRequest searchRequest = new SearchRequest()
                        .indices(esAliases.getIndex())
                        .types(esAliases.getType())
                        .source(searchSourceBuilder)
                        .scroll("1m");
                
                SearchResponse searchResponse = restElasticsearchApi.getSearchResponse(searchRequest);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                
                while (searchHits.length > 0) {
                    if (isNotExistOrCancel(downloadId)) {
                        break;
                    }
                    nums += searchHits.length;
                    log.debug("=============thread" + i + ":get " + nums + "data===================");
                    queue.add(searchHits);
                    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                    scrollRequest.scroll("2m");
                    searchResponse = restElasticsearchApi.searchScroll(scrollRequest);
                    
                    scrollId = searchResponse.getScrollId();
                    searchHits = searchResponse.getHits().getHits();
                }
                restElasticsearchApi.clearScroll(scrollId);
                log.debug("==================thread" + i + "stop==============");
            } catch (Exception e) {
                log.error("查询出错：{}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * 消费者
     */
    class Consumer extends Thread {
        private Boolean flag = true;
        private SXSSFWorkbook workbook;
        private LinkedBlockingQueue<SearchHit[]> queue;
        private List<String> columnNames;
        private List<String> columnFields;
        private String sheetName;
        private Sheet sheet;
        private int rowIndex = 0;
        private int total = 0;
        private int totalNums = 0;
        private int progress = 0;
        private String downloadId;
        private ExportDataFilter exportDataFilter;
        
        public Consumer(SXSSFWorkbook workbook, LinkedBlockingQueue<SearchHit[]> queue, List<String> columnNames, List<String> columnFields, String sheetName, int totalNums, String downloadId, ExportDataFilter exportDataFilter) {
            this.queue = queue;
            this.columnNames = columnNames;
            this.columnFields = columnFields;
            this.sheetName = sheetName;
            this.workbook = workbook;
            this.totalNums = totalNums;
            this.downloadId = downloadId;
            this.exportDataFilter = exportDataFilter;
        }
        
        @Override
        public void run() {
            log.debug("=====================consumer start=================");
            sheet = workbook.createSheet(sheetName);
            rowIndex = excelService.createSimpleHead(workbook, sheet, columnNames, 0);
            while (true) {
                SearchHit[] hits = queue.poll();
                if (null == hits && !flag) {
                    Thread.currentThread().interrupt();
                    log.debug("=====================consumer stop=================");
                    break;
                } else if (null != hits) {
                    List<Map<String, Object>> datas = Arrays.stream(hits).map(searchHit -> exportDataFilter.filter(searchHit)).collect(Collectors.toList());
                    if (rowIndex > 900000) {
                        total += rowIndex;
                        sheet = workbook.createSheet(sheetName + Math.ceil(total / 900000));
                        rowIndex = excelService.createSimpleHead(workbook, sheet, columnNames, 0);
                    }
                    rowIndex = excelService.createData(workbook, sheet, columnFields, datas, rowIndex);
                    if (!isNotExistOrCancel(downloadId)) {
                        progress = (total + rowIndex) / (totalNums / 100);
                        CommonCache.downloadProgress.put(downloadId, progress <= 100 ? progress : 100);
                    }
                }
            }
        }
    }
    
}
