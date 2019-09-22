package com.daishuai.excel.filter;

import org.elasticsearch.search.SearchHit;

import java.util.Map;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 16:33
 */
public interface ExportDataFilter {
    
    /**
     * 对查询结果数据进行过滤
     *
     * @param searchHit
     * @return
     */
    Map<String, Object> filter(SearchHit searchHit);
}
