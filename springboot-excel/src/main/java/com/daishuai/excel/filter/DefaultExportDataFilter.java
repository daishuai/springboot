package com.daishuai.excel.filter;

import org.elasticsearch.search.SearchHit;

import java.util.Map;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 16:37
 */
public class DefaultExportDataFilter implements ExportDataFilter{
    @Override
    public Map<String, Object> filter(SearchHit searchHit) {
        return searchHit.getSourceAsMap();
    }
}
