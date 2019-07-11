package com.daishuai.graphql.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:33
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {
    
    /** ID */
    @Id
    @Column(columnDefinition = "varchar2(32)", nullable = false)
    protected String uuid;
    
    /** 创建时间戳 (单位:秒) */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdTime;
    
    /** 更新时间戳 (单位:秒) */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedTime;
    
    
    public BaseEntity() {
        createdTime = new Date();
        updatedTime = createdTime;
    }
    
    @PreUpdate
    private void doPreUpdate() {
        updatedTime = new Date();
    }
}
