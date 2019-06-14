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
    EMPLOYEE("windows:megacorp", "employee", "雇员"),

    /**
     * 灾情信息
     */
    ZQXX("quanguo:a_fire_zqxx","zqxx", "灾情信息");

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
