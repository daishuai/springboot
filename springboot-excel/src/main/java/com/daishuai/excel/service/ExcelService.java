package com.daishuai.excel.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daishuai.jpa.entity.ExportModelEntity;
import com.daishuai.jpa.repository.ExportModelDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.awt.Color;

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
    
    
    public void commonExport(String sheetName, List<Map<String, Object>> datas, String title, HttpServletResponse response) throws Exception {
        
        ExportModelEntity exportModel = exportModelDao.findBySheetName(sheetName);
        
        String columnName = exportModel.getColumnName();
        String columnField = exportModel.getColumnField();
        String complexColumnName = exportModel.getComplexColumnName();
        List<String> fields = JSON.parseArray(columnField, String.class);
        List<String> names = null;
        if (StringUtils.isNotBlank(columnName)) {
            names = JSON.parseArray(columnName, String.class);
        }
        List<String> complexNames = null;
        List<List> locations = null;
        if (StringUtils.isNotBlank(complexColumnName)) {
            JSONObject jsonObject = JSON.parseObject(complexColumnName);
            complexNames = jsonObject.getJSONArray("columnNames").toJavaList(String.class);
            locations = jsonObject.getJSONArray("locations").toJavaList(List.class);
        }
        //创建SXSSFWorkbook对象
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        //创建SXSSFSheet对象
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        int rowIndex = 0;
        //创建标题
        if (StringUtils.isNotBlank(title)) {
            rowIndex = this.createTitle(workbook, sheet, title, rowIndex, fields.size());
        }
        if (CollectionUtils.isNotEmpty(names)) {
            rowIndex = this.createSimpleHead(workbook, sheet, names, rowIndex);
        }
        
        if (CollectionUtils.isNotEmpty(complexNames)) {
            rowIndex = this.createComplexHead(workbook, sheet, complexNames, locations);
        }
        
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
     * 创建简单的表头
     *
     * @param workbook
     * @param sheet
     * @param columnNames
     * @param headRowIndex
     * @return
     */
    public int createSimpleHead(SXSSFWorkbook workbook, Sheet sheet, List<String> columnNames, int headRowIndex) {
        
        //在sheet中添加表头第0行
        Row row = sheet.createRow(headRowIndex);
        
        CellStyle style = this.createHeadStyle(workbook, (short) 13);
        
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
     * 创建复杂的表头
     *
     * @param workbook
     * @param sheet
     * @param columnNames
     * @param columnLocations
     * @return
     */
    public int createComplexHead(SXSSFWorkbook workbook, Sheet sheet, List<String> columnNames, List<List> columnLocations) {
        int rowIndex = 0;
        Cell cell;
        XSSFColor backgroundColor = new XSSFColor(new Color(204, 255, 255));
        CellStyle headStyle = this.createHeadStyle(workbook, backgroundColor.getIndex());
        for (int i = 0; i < columnNames.size(); i++) {
            List<Integer> locations = columnLocations.get(i);
            int firstRowIndex = locations.get(0);
            int lastRowIndex = locations.get(1);
            int firstColIndex = locations.get(2);
            int lastColIndex = locations.get(3);
            Row firstRow = sheet.getRow(firstRowIndex) == null ? sheet.createRow(firstRowIndex) : sheet.getRow(firstRowIndex);
            Row lastRow = sheet.getRow(lastRowIndex) == null ? sheet.createRow(lastRowIndex) : sheet.getRow(lastRowIndex);
            if (rowIndex < lastRowIndex) {
                rowIndex = lastRowIndex;
            }
            if (firstRowIndex == lastRowIndex && firstColIndex == lastColIndex) {
                cell = firstRow.createCell(firstColIndex);
                cell.setCellValue(columnNames.get(i));
                cell.setCellStyle(headStyle);
                sheet.setColumnWidth(firstColIndex, 1200 * columnNames.get(i).length());
            } else {
                cell = firstRow.createCell(firstColIndex);
                cell.setCellValue(columnNames.get(i));
                cell.setCellStyle(headStyle);
                //合并单元格
                CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRowIndex, lastRowIndex, firstColIndex, lastColIndex);
                sheet.addMergedRegion(cellRangeAddress);
                sheet.setColumnWidth(firstColIndex, 1200 * columnNames.get(i).length());
                RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
            }
        }
        return ++rowIndex;
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
        Font titleFont = workbook.createFont();
        //fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleCellStyle.setFont(titleFont);
        return titleCellStyle;
    }
    
    /**
     * 列标题样式
     *
     * @param workbook
     * @param backgroundColor 背景色
     * @return
     */
    private CellStyle createHeadStyle(SXSSFWorkbook workbook, short backgroundColor) {
        //创建单元格，并设置值表头 设置表头居中
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        // 创建一个居中格式 自动换行
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(backgroundColor);
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
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
