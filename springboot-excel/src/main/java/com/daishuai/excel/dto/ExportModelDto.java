package com.daishuai.excel.dto;

import com.daishuai.excel.filter.DefaultExportDataFilter;
import com.daishuai.excel.filter.ExportDataFilter;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 16:23
 */
@Data
public class ExportModelDto {
    
    /**
     * 列名称
     */
    private List<String> columnNames;
    
    /**
     * 列对应的字段
     */
    private List<String> columnFields;
    
    /**
     * 查询后的数据进行过滤
     */
    private ExportDataFilter exportDataFilter = new DefaultExportDataFilter();
    
    /**
     * 表单名
     */
    private String sheetName;
    
    /**
     * 查询条件
     */
    private QueryBuilder query;
    
    /**
     * 排序条件
     */
    private SortBuilder sortBuilder;
    
    /**
     * 当前下载的标记
     */
    private String downloadId;
}
