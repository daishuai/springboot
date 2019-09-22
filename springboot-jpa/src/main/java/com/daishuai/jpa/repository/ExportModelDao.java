package com.daishuai.jpa.repository;

import com.daishuai.jpa.entity.ExportModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 10:36
 */
public interface ExportModelDao extends JpaRepository<ExportModelEntity, String> {
    
    /**
     * 根据表单名称查询
     *
     * @param sheetName
     * @return
     */
    ExportModelEntity findBySheetName(String sheetName);
}
