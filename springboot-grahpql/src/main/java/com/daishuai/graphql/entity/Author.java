package com.daishuai.graphql.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Daishuai
 * @description TODO
 * @date 2019/7/11 15:35
 */
@Table(name = "author")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Author extends BaseEntity {
    
    @Column(columnDefinition = "varchar2(50)")
    private String firstName;
    
    @Column(columnDefinition = "varchar2(50)")
    private String lastName;
}
