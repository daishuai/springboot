package com.daishuai.es.enums;

/**
 * @author Daishuai
 * @description ES索引
 * @date 2019/6/7 13:04
 */
public enum EsAliases {

    /**
     * 雇员
     */
    EMPLOYEE_WINDOWS("windows:megacorp", "employee", "雇员"),

    /**
     * 雇员
     */
    EMPLOYEE_CENTOS("centos:megacorp", "employee", "雇员"),

    /**
     * 灾情信息
     */
    ZQXX_QUANGUO("quanguo:a_fire_zqxx","zqxx", "灾情信息"),
    
    XFDW("a_fire_xfdw", "xfdw", "消防单位"),
    
    RYXX("a_fire_ryxx", "ryxx", "人员信息"),

    /**
     * 灾情信息
     */
    ZQXX_JIANGSU("jiangsu:a_fire_zqxx","zqxx", "灾情信息");

    EsAliases(String index, String type, String description) {
        this.index = index;
        this.type = type;
        this.description = description;
    }

    private String index;

    private String type;

    private String description;

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
