package com.daishuai.es.enums;

/**
 * @author admin
 * @version 1.0.0
 * @description ES索引枚举
 * @createTime 2022-08-07 22:25:47
 */
public enum CommonEsAliases implements EsAliases {



    ZQXX("a_fire_zqxx", "zqxx", "fire_zqxx_write", "fire_zqxx_read", "灾情信息");

    private final String index;

    private final String type;

    private final String write;

    private final String read;

    private final String desc;

    CommonEsAliases(String index, String type, String write, String read, String desc) {
        this.index = index;
        this.type = type;
        this.write = write;
        this.read = read;
        this.desc = desc;
    }

    @Override
    public String getIndex() {
        return this.index;
    }

    @Override
    public String getRead() {
        return this.read;
    }

    @Override
    public String getWrite() {
        return this.write;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
