package com.daishuai.jpa.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 10:29
 */
@Data
@Table(name = "export_model")
@Entity
public class ExportModelEntity {
    
    @Id
    private String uuid;
    
    private String title;
    
    private String sheetName;
    
    private String columnName;
    
    private String columnField;
    
    private String description;
    
    private String complexColumnName;
}
