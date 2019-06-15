package com.daishuai.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Set;

/**
 * @author Daishuai
 * @description 雇员
 * @date 2019/6/7 12:55
 */
@Data
public class EmployeeEntity {

    /**
     * 姓
     */
    @JSONField(name = "first_name")
    private String firstName;

    /**
     * 名
     */
    @JSONField(name = "last_name")
    private String lastName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 介绍
     */
    private String about;

    /**
     * 兴趣
     */
    private Set<String> interests;

    /**
     * 数据来源
     */
    private String datasource;
}
