package com.daishuai.excel.service;

import com.alibaba.fastjson.JSON;
import com.daishuai.jpa.entity.ExportModelEntity;
import com.daishuai.jpa.repository.ExportModelDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 10:07
 */
@Slf4j
@Service
public class ExcelService {
    
    
    @Autowired
    private ExportModelDao exportModelDao;
    
    
    public void commonExport(String sheetName, List<Map<String, Object>> datas, HttpServletResponse response) throws Exception {
        
        ExportModelEntity exportModel = exportModelDao.findBySheetName(sheetName);
        
        String title = exportModel.getTitle();
        String columnName = exportModel.getColumnName();
        String columnField = exportModel.getColumnField();
        List<String> names = JSON.parseArray(columnName, String.class);
        List<String> fields = JSON.parseArray(columnField, String.class);
        //创建SXSSFWorkbook对象
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        //创建SXSSFSheet对象
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        int rowIndex = 0;
        //创建标题
        if (StringUtils.isNotBlank(title)) {
            rowIndex = this.createTitle(workbook, sheet, title, rowIndex, names.size());
        }
        rowIndex = this.createHead(workbook, sheet, names, rowIndex);
        
        rowIndex = this.createData(workbook, sheet, fields, datas, rowIndex);
        
        //输出Excel文件
        //FileOutputStream output = new FileOutputStream("d:\\workbook.xlsx");
        //workbook.write(output);
        //output.flush();
        
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            response.setHeader("Content-Disposition", "attachment;Filename=" + System.currentTimeMillis() + ".xlsx");
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception ex) {
            log.error("导出Excel出现严重异常，异常信息：{}", ex.getMessage(), ex);
        }
    }
    
    
    /**
     * 创建标题
     *
     * @param workbook
     * @param sheet
     * @param title
     * @param titleRowIndex
     * @param columnSize
     * @return
     */
    public int createTitle(SXSSFWorkbook workbook, Sheet sheet, String title, int titleRowIndex, int columnSize) {
        // 标题样式（加粗，垂直居中）
        CellStyle titleCellStyle = this.createTitleStyle(workbook);
        
        //在第0行创建rows  (表标题)
        Row tileRow = sheet.createRow(0);
        tileRow.setHeightInPoints(30);
        Cell titleCell = tileRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleCellStyle);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnSize - 1));
        return ++titleRowIndex;
    }
    
    /**
     * 创建表头
     *
     * @param workbook
     * @param sheet
     * @param columnNames
     * @param headRowIndex
     * @return
     */
    public int createHead(SXSSFWorkbook workbook, Sheet sheet, List<String> columnNames, int headRowIndex) {
        
        //在sheet中添加表头第0行
        Row row = sheet.createRow(headRowIndex);
        
        CellStyle style = this.createHeadStyle(workbook);
        
        Cell cell;
        for (int i = 0; i < columnNames.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(columnNames.get(i));
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 1200 * columnNames.get(i).length());
        }
        return ++headRowIndex;
    }
    
    /**
     * 创建数据
     *
     * @param workbook
     * @param sheet
     * @param columnFields
     * @param datas
     * @param rowIndex
     * @return
     */
    public int createData(SXSSFWorkbook workbook, Sheet sheet, List<String> columnFields, List<Map<String, Object>> datas, int rowIndex) {
        CellStyle valueStyle = this.createValueStyle(workbook);
        for (Map<String, Object> firstMap : datas) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(30);
            for (int i = 0; i < columnFields.size(); i++) {
                String columnName = columnFields.get(i);
                String[] names = StringUtils.split(columnName, "//.");
                Object object = firstMap.get(names[0]);
                if (object == null) {
                    continue;
                }
                Cell cell = row.createCell(i);
                cell.setCellStyle(valueStyle);
                if (object instanceof Map && names.length > 1) {
                    Map<String, Object> secondMap = (Map<String, Object>) object;
                    object = secondMap.get(names[1]);
                }
                cell.setCellValue(object.toString());
            }
        }
        return rowIndex;
    }
    
    
    /**
     * 标题样式
     *
     * @param workbook
     * @return
     */
    private CellStyle createTitleStyle(SXSSFWorkbook workbook) {
        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font tileFont = workbook.createFont();
        //fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        tileFont.setBold(true);
        tileFont.setFontHeightInPoints((short) 16);
        titleCellStyle.setFont(tileFont);
        return titleCellStyle;
    }
    
    /**
     * 列标题样式
     *
     * @param workbook
     * @return
     */
    private CellStyle createHeadStyle(SXSSFWorkbook workbook) {
        //创建单元格，并设置值表头 设置表头居中
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        // 创建一个居中格式 自动换行
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor((short) 13);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }
    
    /**
     * 每个数据项的单元格样式
     *
     * @param workbook
     * @return
     */
    private CellStyle createValueStyle(SXSSFWorkbook workbook) {
        CellStyle styleText = workbook.createCellStyle();
        // 创建一个居中格式 自动换行
        styleText.setVerticalAlignment(VerticalAlignment.CENTER);
        styleText.setWrapText(true);
        return styleText;
    }
    
}
