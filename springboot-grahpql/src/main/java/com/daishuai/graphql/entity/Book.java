package com.daishuai.graphql.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:34
 */
@Entity
@Data
@Table(name = "book")
@EqualsAndHashCode(callSuper = false)
public class Book extends BaseEntity {
    @Column(columnDefinition = "varchar2(50)")
    private String title;
    
    private String isbn;
    
    private Integer pageCount;
    
    private String authorId;
}
